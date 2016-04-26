/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import static crx.converter.engine.PharmMLTypeChecker.isContinuousCovariate;
import static crx.converter.engine.PharmMLTypeChecker.isPKMacroList;
import static crx.converter.engine.PharmMLTypeChecker.isPharmMLObject;
import static crx.converter.engine.PharmMLTypeChecker.isRootType;
import static crx.converter.engine.PharmMLTypeChecker.isSymbol;
import static crx.converter.engine.PharmMLTypeChecker.isVariabilityLevelDefinition;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import eu.ddmore.libpharmml.ILibPharmML;
import eu.ddmore.libpharmml.IPharmMLResource;
import eu.ddmore.libpharmml.PharmMlFactory;
import eu.ddmore.libpharmml.dom.IndependentVariable;
import eu.ddmore.libpharmml.dom.PharmML;
import eu.ddmore.libpharmml.dom.commontypes.FunctionDefinition;
import eu.ddmore.libpharmml.dom.commontypes.LevelReference;
import eu.ddmore.libpharmml.dom.commontypes.OidRef;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLElement;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.Symbol;
import eu.ddmore.libpharmml.dom.commontypes.SymbolRef;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.dataset.ColumnReference;
import eu.ddmore.libpharmml.dom.dataset.DataSet;
import eu.ddmore.libpharmml.dom.modeldefn.CategoricalData;
import eu.ddmore.libpharmml.dom.modeldefn.CategoricalPMF;
import eu.ddmore.libpharmml.dom.modeldefn.CommonDiscreteVariable;
import eu.ddmore.libpharmml.dom.modeldefn.ContinuousCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.ContinuousObservationModel;
import eu.ddmore.libpharmml.dom.modeldefn.CountData;
import eu.ddmore.libpharmml.dom.modeldefn.CountPMF;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateModel;
import eu.ddmore.libpharmml.dom.modeldefn.CovariateTransformation;
import eu.ddmore.libpharmml.dom.modeldefn.Discrete;
import eu.ddmore.libpharmml.dom.modeldefn.DiscreteDataParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ModelDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.ObservationError;
import eu.ddmore.libpharmml.dom.modeldefn.ObservationModel;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterModel;
import eu.ddmore.libpharmml.dom.modeldefn.ParameterRandomVariable;
import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.dom.modeldefn.ProbabilityAssignment;
import eu.ddmore.libpharmml.dom.modeldefn.StructuralModel;
import eu.ddmore.libpharmml.dom.modeldefn.TTEFunction;
import eu.ddmore.libpharmml.dom.modeldefn.TimeToEventData;
import eu.ddmore.libpharmml.dom.modeldefn.TransformedCovariate;
import eu.ddmore.libpharmml.dom.modeldefn.TransitionMatrix;
import eu.ddmore.libpharmml.dom.modeldefn.VariabilityDefnBlock;
import eu.ddmore.libpharmml.dom.modeldefn.VariabilityLevelDefinition;
import eu.ddmore.libpharmml.dom.modeldefn.pkmacro.PKMacroList;
import eu.ddmore.libpharmml.dom.modellingsteps.CommonModellingStep;
import eu.ddmore.libpharmml.dom.modellingsteps.ExternalDataSetReference;
import eu.ddmore.libpharmml.dom.modellingsteps.ModellingSteps;
import eu.ddmore.libpharmml.dom.tags.PharmMLObject;
import eu.ddmore.libpharmml.dom.trialdesign.Administration;
import eu.ddmore.libpharmml.dom.trialdesign.ArmDefinition;
import eu.ddmore.libpharmml.dom.trialdesign.Arms;
import eu.ddmore.libpharmml.dom.trialdesign.ExternalDataSet;
import eu.ddmore.libpharmml.dom.trialdesign.Interventions;
import eu.ddmore.libpharmml.dom.trialdesign.Observations;
import eu.ddmore.libpharmml.dom.trialdesign.Occasion;
import eu.ddmore.libpharmml.dom.trialdesign.OccasionList;
import eu.ddmore.libpharmml.dom.trialdesign.OccasionSequence;
import eu.ddmore.libpharmml.dom.trialdesign.TrialDesign;
import eu.ddmore.libpharmml.dom.uncertml.VarRefType;

/**
 * The purpose of this class is to return 'named' elements from a PharmML DOM.<br/>
 * This class is the PharmML equivalent of XPath.
 *
 */
public class Accessor {
	/**
	 * Read a level reference from a random variable.
	 * @param rv Random Variable
	 * @return LevelReference
	 */
	public static LevelReference readLevel(ParameterRandomVariable rv) {
		if (rv == null) return null;
		
		List<LevelReference> levels = rv.getListOfVariabilityReference();
		if (levels != null) if (levels.size() > 0) return levels.get(0);
		
		return null;
	}
	
	public static CommonModellingStep readStep(JAXBElement<? extends CommonModellingStep> tag) {
		CommonModellingStep value = null;
		if (tag != null) value = tag.getValue();
		
		return value;
	}
	
	private List<CategoryRef_> categories = new ArrayList<CategoryRef_>();
	private Map<PharmMLRootType, List<CategoryRef_>> category_map = new HashMap<PharmMLRootType, List<CategoryRef_>>();
	private String currentblkId = null;
	private PharmML dom = null; 
	private ILibPharmML lib = null;
	private Map<String, PharmMLRootType> machine_generated_elements = new HashMap<String, PharmMLRootType>();
	private List<MatrixDeclaration> matrices = new ArrayList<MatrixDeclaration>();
	private IPharmMLResource res = null;
	
	/**
	 * Constructor to link an accessor instance to a PharmML DOM.
	 * @param  dom_  PharmML DOM (in-memory model)
	 * @throws NullPointerException
	 * @see eu.ddmore.libpharmml.dom.PharmML
	 */
	public Accessor(PharmML dom_) {
		if (dom_ == null) throw new NullPointerException("The model DOM cannot be null.");
			
		dom = dom_;
	}
	
	/**
	 * Constructor to link an accessor instance to a PharmML XML document.
	 * @param  xmlFilePath  PharmML DOM (in-memory model)
	 * @throws NullPointerException
	 * @see eu.ddmore.libpharmml.dom.PharmML
	 */
	public Accessor(String xmlFilePath) throws Exception {
		lib = PharmMlFactory.getInstance().createLibPharmML();
		
		InputStream in = new FileInputStream(xmlFilePath);
		res = lib.createDomFromResource(in);
		in.close();
		
		dom = res.getDom(); 
	}
	
	private boolean containsElement(PharmMLRootType element, CategoricalData cd) {
		if (cd == null || element == null) return false;
		
		CommonDiscreteVariable cv = cd.getCategoryVariable();
		if (element.equals(cv)) return true;
		
		List<CommonDiscreteVariable> categories = cd.getListOfCategories();
		if (categories != null) 
			for (CommonDiscreteVariable category : categories) 
				if (element.equals(category)) return true;
		
		List<CommonDiscreteVariable> initial_states = cd.getListOfInitialStateVariable();
		if (initial_states != null)
			for (CommonDiscreteVariable initial_state : initial_states) 
				if (element.equals(initial_state)) return true;
		
		List<CategoricalPMF> pmfs = cd.getListOfPMF();
		if (pmfs != null)
			for (CategoricalPMF pmf : pmfs)
				if (element.equals(pmf)) return true;
		
		List<CommonDiscreteVariable> previous_states = cd.getListOfPreviousStateVariable();
		if (previous_states != null)
			for (CommonDiscreteVariable previous_state : previous_states) 
				if (element.equals(previous_state)) return true;
		
		List<ProbabilityAssignment> probs = cd.getListOfProbabilityAssignment();
		if (probs != null) {
			for (ProbabilityAssignment prob : probs) {
				if (element.equals(prob)) return true;
			}
		}
		
		TransitionMatrix trans = cd.getTransitionMatrix();
		if (element.equals(trans)) return true;
				
		return false;
	}
	
	private boolean containsElement(PharmMLRootType element, CountData cd) {
		if (cd == null || element == null) return false;
		
		CommonDiscreteVariable cc = cd.getCountVariable();
		if (element.equals(cc)) return true;
		
		DiscreteDataParameter dp = cd.getDispersionParameter();
		if (element.equals(dp)) return true;
		
		List<CommonDiscreteVariable> initial_counts = cd.getListOfInitialCountVariable();
		if (initial_counts != null)
			for (CommonDiscreteVariable initial_count : initial_counts) 
				if (element.equals(initial_count)) return true;
		
		List<DiscreteDataParameter> ips = cd.getListOfIntensityParameter();
		if (ips != null) for (DiscreteDataParameter ip : ips) if (element.equals(ip)) return true; 
		
		List<CountPMF> pmfs = cd.getListOfPMF();
		if (pmfs != null)
			for (CountPMF pmf : pmfs)
				if (element.equals(pmf)) return true;
		
		List<CommonDiscreteVariable> pcvs = cd.getListOfPreviousCountVariable();
		if (ips != null) {
			for (CommonDiscreteVariable pcv : pcvs) if (element.equals(pcv)) return true;
		}
		
		DiscreteDataParameter mpp = cd.getMixtureProbabilityParameter();
		if (element.equals(mpp)) return true;
		
		CommonDiscreteVariable nc =  cd.getNumberCounts();
		if (element.equals(nc)) return true;
		
		DiscreteDataParameter zpp = cd.getZeroProbabilityParameter();
		if (element.equals(zpp)) return true;
		
		return false;
	}
	
	private boolean containsElement(PharmMLRootType element, TimeToEventData tte_data) {
		if (tte_data == null || element == null) return false;
		
		CommonDiscreteVariable ev = tte_data.getEventVariable();
		if (element.equals(ev)) return true;
		
		
		List<TTEFunction> hazard_funcs = tte_data.getListOfHazardFunction();
		if (hazard_funcs != null) for (TTEFunction hazard_func : hazard_funcs) if (element.equals(hazard_func)) return true;
		
		List<TTEFunction> survival_funcs = tte_data.getListOfSurvivalFunction();
		if (survival_funcs != null) for (TTEFunction survival_func : survival_funcs) if (element.equals(survival_func)) return true;
		
		return false;
	}
	
	private boolean containsIdentifer(String symbId, CategoricalData cd) {
		if (cd == null || symbId == null) return false;
		
		CommonDiscreteVariable cv = cd.getCategoryVariable();
		if (cv != null) if (symbId.equals(cv.getSymbId())) return true;
		
		List<CommonDiscreteVariable> categories = cd.getListOfCategories();
		if (categories != null) 
			for (CommonDiscreteVariable category : categories) 
				if (category != null) if (symbId.equals(category.getSymbId())) return true;
		
		List<CommonDiscreteVariable> initial_states = cd.getListOfInitialStateVariable();
		if (initial_states != null)
			for (CommonDiscreteVariable initial_state : initial_states)
				if (initial_state != null) if (symbId.equals(initial_state.getSymbId())) return true;
		
		List<CommonDiscreteVariable> states = cd.getListOfPreviousStateVariable();
		if (states != null)
			for (CommonDiscreteVariable state : states)
				if (state != null) if (symbId.equals(state.getSymbId())) return true;
		
		TransitionMatrix trans = cd.getTransitionMatrix();
		if (trans != null) if (symbId.equals(trans.getSymbId())) return true;
				
		return false;
	}
	
	private boolean containsIdentifer(String symbId, TimeToEventData tte_data) {
		if (tte_data == null || symbId == null) return false;
		
		CommonDiscreteVariable ev = tte_data.getEventVariable();
		if (ev != null) if (symbId.equals(ev.getSymbId())) return true;
		
		
		List<TTEFunction> hazard_funcs = tte_data.getListOfHazardFunction();
		if (hazard_funcs != null)
			for (TTEFunction hazard_func : hazard_funcs)
				if (hazard_func != null) if (symbId.equals(hazard_func.getSymbId())) return true;
		
		List<TTEFunction> survival_funcs = tte_data.getListOfSurvivalFunction();
		if (survival_funcs != null)
			for (TTEFunction survival_func : survival_funcs)
				if (survival_func != null) if (symbId.equals(survival_func.getSymbId())) return true;
		
		return false;
	}
	  
	private boolean containsIdentifier(String symbId, CountData cd) {
		if (cd == null || symbId == null) return false;
		
		/*
		
DiscreteDataParameter 	getOverDispersionParameter()
(Optional) Definition of over-dispersion parameter, usualy 'delta', for GP models.
DiscreteDataParameter 	getZeroProbabilityParameter()
		 */
		
		CommonDiscreteVariable cc = cd.getCountVariable();
		if (cc != null) {
			String currentSymbolId = cc.getSymbId();
			if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return true;
		}
		
		DiscreteDataParameter dp = cd.getDispersionParameter();
		if (dp != null) {
			String currentSymbolId = dp.getSymbId();
			if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return true;
		}
		
		List<CommonDiscreteVariable> initial_counts = cd.getListOfInitialCountVariable();
		if (initial_counts != null) {
			for (CommonDiscreteVariable cdv : initial_counts) {
				if (cdv == null) continue;
				String currentSymbolId = cdv.getSymbId();
				if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return true;
			}
		}
		
		List<DiscreteDataParameter> ips = cd.getListOfIntensityParameter();
		if (ips != null) {
			for (DiscreteDataParameter ip : ips) {
				if (ip == null) continue;
				String currentSymbolId = ip.getSymbId();
				if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return true;
			}
		}
		
		List<CommonDiscreteVariable> pcvs = cd.getListOfPreviousCountVariable();
		if (ips != null) {
			for (CommonDiscreteVariable pcv : pcvs) {
				if (pcv == null) continue;
				String currentSymbolId = pcv.getSymbId();
				if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return true;
			}
		}
		
		DiscreteDataParameter mpp = cd.getMixtureProbabilityParameter();
		if (mpp != null) {
			String currentSymbolId = mpp.getSymbId();
			if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return true;
		}
		
		CommonDiscreteVariable nc = cd.getNumberCounts();
		if (nc != null) {
			String currentSymbolId = nc.getSymbId();
			if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return true;
		}

		DiscreteDataParameter odp = cd.getOverDispersionParameter();
		if (odp != null) {
			String currentSymbolId = odp.getSymbId();
			if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return true;
		}
		
		DiscreteDataParameter zpp = cd.getZeroProbabilityParameter();
		if (zpp != null) {
			String currentSymbolId = zpp.getSymbId();
			if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return true;
		}
		
		return false;
	}
	
	private boolean equals_(String s1, String s2) {
		if (s1 != null && s2 != null) return s1.equals(s2);
		else return false;
	}
	
	/**
	 * Fetch a category list associated with an model element.
	 * @param element Model element
	 * @return java.util.List<CategoryRef>
	 */
	public List<CategoryRef_> fetchCategoryList(PharmMLRootType element) {
		if (element != null) {
			if (category_map.containsKey(element)) return category_map.get(element);
		}
		
		return null;
	}
	
	/**
	 * Fetch a column reference from a data set based on a reference.
	 * @param ds Data Set
	 * @param ref Column Reference
	 * @return eu.ddmore.libpharmml.dom.dataset.ColumnDefinition
	 */
	public ColumnDefinition fetchColumnDefinition(DataSet ds, ColumnReference ref) {
		if (ds != null && ref != null) {
			if (ref.getColumnRef() != null) ref = ref.getColumnRef();
			
			String col_name = ref.getColumnIdRef();
			if (col_name == null) return null;
			
			List<ColumnDefinition> cols = ds.getDefinition().getListOfColumn();
			if (cols == null) return null;
			
			for (ColumnDefinition col : cols) {
				if (col == null) continue;
				if (col_name.equals(col.getColumnId())) return col;
			}
		}
		
		return null;
	}
	
	/**
	 * Fetch a column reference from a data set based on a reference.
	 * @param ds Data Set
	 * @param colName Column Name
	 * @return eu.ddmore.libpharmml.dom.dataset.ColumnDefinition
	 */
	public ColumnDefinition fetchColumnDefinition(DataSet ds, String colName) { return fetchColumnDefinition(ds, new ColumnReference(colName)); }
	
	/**
	 * Fetch a model element based on a level reference.
	 * @param ref Variable Reference.
	 * @return PharmMLRootType
	 */
	public PharmMLRootType fetchElement(LevelReference ref) {
		if (ref == null) return null;
		
		SymbolRef sref = ref.getSymbRef();
		if (sref == null) return null;
		return fetchElement(sref);
	}
	
	/**
	 * Return model element based on an 'object' reference.
	 * @param ref An 'object' reference.
	 * @return PharmMLRootType
	 * @see OidRef
	 */
	public PharmMLRootType fetchElement(OidRef ref) {
		if (ref == null) return null;
		
		String oid = ref.getOidRef();
		if (oid == null) return null;
		
		PharmMLRootType element = fetchStep(oid);
		if (element != null) return element;
		
		TrialDesign td = dom.getTrialDesign();
		element = oidSearchTrialDesign(oid, td);
		if (element != null) return element;
		
		return null;
	}
	
	/**
	 * Fetch model element based on a symbol identifier.
	 * @param symbId Model element name
	 * @return PharmMLRootType
	 */
	public PharmMLRootType fetchElement(String symbId) {
		PharmMLRootType element = null;
		
		if (symbId == null) return null;
		
		if (currentblkId != null) {
			element = fetchElement(currentblkId, symbId);
			if (element != null) return element;
		}
		
		element = fetchIndependentVariable(symbId);
		if (element != null) return element;
		
		if (machine_generated_elements.containsKey(symbId)) return machine_generated_elements.get(symbId);
		if ((element = searchMatrixDeclarations(symbId)) != null) return element;
		if ((element = searchCategoryRefs(symbId)) != null) return element;
		
		ModelDefinition md = dom.getModelDefinition();
		if (md == null) return element;
		
		if ((element = searchVariabilityModel(md, symbId)) != null) return element;
		if ((element = searchObservationModel(md, symbId)) != null) return element;
		if ((element = searchParameterModel(md, symbId)) != null) return element;
		if ((element = searchStructuralModel(md, symbId)) != null) return element;
		if ((element = searchCovariateModel(md, symbId)) != null) return element;
		
		return element;
	}
	
	/**
	 * Fetch model element using a block and symbol name.
	 * @param blkId Block identifier.
	 * @param symbId Symbol identifier.
	 * @return PharmMLRootType
	 */
	public PharmMLRootType fetchElement(String blkId, String symbId) {
		SymbolRef ref = new SymbolRef();
		ref.setBlkIdRef(blkId);
		ref.setSymbIdRef(symbId);
		
		return fetchElement(ref);
	}
	
	/**
	 * Fetch model element based on a symbol reference.
	 * @param ref Symbol reference
	 * @return PharmMLRootType
	 */
	public PharmMLRootType fetchElement(SymbolRef ref) {
		PharmMLRootType element = null;
		if (ref == null) throw new NullPointerException("Element reference is NULL.");
		String blkId = ref.getBlkIdRef();
		String symbId = ref.getSymbIdRef();
		
		if (symbId == null) throw new NullPointerException("Symbol ID is NULL.");
		element = fetchIndependentVariable(symbId);
		if (element != null) return element;

		if (blkId == null) return fetchElement(symbId);
		
		if (machine_generated_elements.containsKey(symbId)) return machine_generated_elements.get(symbId);
		if ((element = searchMatrixDeclarations(blkId, symbId)) != null) return element;
		if ((element = searchCategoryRefs(symbId)) != null) return element;
		
		ModelDefinition md = dom.getModelDefinition();
		if (md == null) return element;

		if ((element = searchVariabilityModel(md, symbId, blkId)) != null) return element;
		if ((element = searchObservationModel(md, symbId, blkId)) != null) return element;
		if ((element = searchParameterModel(md, symbId, blkId)) != null) return element;
		if ((element = searchStructuralModel(md, symbId, blkId)) != null) return element;
		if ((element = searchCovariateModel(md, symbId, blkId)) != null) return element;
		
		return element;
	}
	
	/**
	 * Fetch a model element based on a variable reference.
	 * @param ref Variable Reference.
	 * @return PharmMLRootType
	 */
	public PharmMLRootType fetchElement(VarRefType ref) {
		PharmMLRootType element = null;
		
		if (ref != null) {
			if (currentblkId != null) {
				element = fetchElement(currentblkId, ref.getVarId());
				if (element != null) return element;
			}
			element = fetchElement(ref.getVarId());
		}
		
		return element;
	}
	
	/**
	 * Fetch an External dataset by an reference.
	 * @param ref Dataset reference
	 * @return eu.ddmore.libpharmml.dom.modellingsteps.ExternalDataSet
	 */
	
	public ExternalDataSet fetchExternalDataSet(ExternalDataSetReference ref) {
		TrialDesign td = dom.getTrialDesign();
		if (td == null || ref == null) return null;
		
		OidRef oidRef = ref.getOidRef();
		if (oidRef == null) return null;
		String oid = oidRef.getOidRef();
		if (oid == null) return null;
		
		List<ExternalDataSet> dss = td.getListOfExternalDataSet();
		if (dss != null) {
			for (ExternalDataSet ds : dss) {
				if (ds == null) continue;
				String ds_oid = ds.getOid();
				if (oid.equals(ds_oid)) return ds;
			}
		}
		
		return null;
	}
	
	/**
	 * Fetch an External dataset by an reference.
	 * @param oid External Dataset reference
	 * @return eu.ddmore.libpharmml.dom.modellingsteps.ExternalDataSet
	 */
	public ExternalDataSet fetchExternalDataSet(String oid) {
		if (oid == null) return null;
		
		TrialDesign td = dom.getTrialDesign();
		if (td == null) return null;
		
		List<ExternalDataSet> dss = td.getListOfExternalDataSet();
		if (dss != null) {
			for (ExternalDataSet ds : dss) {
				if (ds == null) continue;
				String ds_oid = ds.getOid();
				if (oid.equals(ds_oid)) return ds;
			}
		}
		
		return null;
	}
	
	private PharmMLRootType fetchIndependentVariable(String name) {
		if (name != null) {
			for (IndependentVariable ic : dom.getListOfIndependentVariable()) {
				if (ic == null) continue;
				String symbId = ic.getSymbId();
				if (symbId != null) {
					if (symbId.equals(name)) return ic;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Fetch a modelling step by object identifier.
	 * @param oid
	 * @return CommonModellingStep
	 */
	public CommonModellingStep fetchStep(String oid) {
		ModellingSteps modelling_steps = dom.getModellingSteps();
		
		if (modelling_steps != null) {
			for (JAXBElement<? extends CommonModellingStep> tag : modelling_steps.getCommonModellingStep()) {
				if (tag == null) continue;
				CommonModellingStep step = tag.getValue();
				String blkOid = step.getOid();
				if (blkOid == null) continue;
				if (blkOid.equalsIgnoreCase(oid)) return step;
			}
		}
		
		return null;
	}
	
	/**
	 * Fetch a column reference from a data set based on a reference.
	 * @param ref Symbol Reference
	 * @return StructuralModel
	 */
	public StructuralModel fetchStructuralModel(SymbolRef ref) {
		if (ref != null) {
			String blkId = ref.getBlkIdRef();
			if (blkId == null) return null;
			
			ModelDefinition def = dom.getModelDefinition();
			if (def == null) return null;
			
			List<StructuralModel> sms = def.getListOfStructuralModel();
			if (sms == null) return null;
			
			for (StructuralModel sm : sms) {
				if (sm == null) continue;
				String currentBlkId = sm.getBlkId();
				if (blkId.equals(currentBlkId)) return sm;
			}
		}
		
		return null;
	}
	
	/**
	 * Get a Block identifier hosting a model element.
	 * @param element Model Element
	 * @return String or null
	 */
	public String getBlockId(PharmMLRootType element) {
		if (element == null) return null;
		
		ModelDefinition md = dom.getModelDefinition();
		if (md == null) return null;
		
		if (isVariabilityLevelDefinition(element)) {
			List<VariabilityDefnBlock> vdbs = md.getListOfVariabilityModel();
			if (vdbs != null) {
				for (VariabilityDefnBlock vdb : vdbs) {
					if (vdb == null) continue;
					for (VariabilityLevelDefinition level : vdb.getLevel()) {
						if (level == null) continue;
						if (level.equals(element)) return vdb.getBlkId();
					}
				}
			}
		}
		
		if (isContinuousCovariate(element)) {
			List<CovariateModel> cmts = md.getListOfCovariateModel();
			if (cmts != null) {
				for (CovariateModel cmt : cmts) {
					if (cmt == null) continue;
					for (CovariateDefinition cov : cmt.getListOfCovariate()) {
						if (cov == null) continue;
						ContinuousCovariate ccov = cov.getContinuous();
						if (ccov == null) continue;
						if (ccov.equals(element)) return cmt.getBlkId();
					}
				}						
			}
		}
		
		for (CategoryRef_ category : categories) {
			if (category == null) continue;
			if (category.equals(element)) return category.getBlkId(); 
		}
		
		List<ObservationModel> omts = md.getListOfObservationModel();
		if (omts != null) {
			for (ObservationModel omt : omts) {
				if (omt == null) continue;
				
				ContinuousObservationModel com = omt.getContinuousData();
				if (com != null) {
					for (Object o : com.getListOfObservationModelElement()) 
						if (element.equals(o)) return omt.getBlkId();
						
					ObservationError oe = com.getObservationError();
					if (oe != null) if (element.equals(oe)) return omt.getBlkId();
				}
				
				Discrete discrete = omt.getDiscrete();
				if (discrete != null) {
					if (containsElement(element, discrete.getCountData())) return omt.getBlkId();
					else if (containsElement(element, discrete.getCategoricalData())) return omt.getBlkId();
					else if (containsElement(element, discrete.getTimeToEventData())) return omt.getBlkId();
				}
			}
		}
		
		List<ParameterModel> pmts = md.getListOfParameterModel();
		if (pmts != null) {
			for (ParameterModel pmt : pmts) {
				if (pmt == null) continue;	
				for (PharmMLElement o : pmt.getListOfParameterModelElements()) {
					if (element.equals(o)) return pmt.getBlkId();
				}
			}
		}
		
		List<StructuralModel> smts = md.getListOfStructuralModel();
		if (smts != null) {
			for (StructuralModel smt : smts) {
				if (smt == null) continue;
				for (PharmMLElement o : smt.getListOfStructuralModelElements()) {
					if (element.equals(o)) return smt.getBlkId();
				}
			}
		}
		
		
		// Search for parameters in the covariate model.
		List<CovariateModel> cmts = md.getListOfCovariateModel();
		if (cmts != null) {
			for (CovariateModel cmt : cmts) {
				if (cmt == null) continue;
				List<PopulationParameter> params = cmt.getListOfPopulationParameter();
				if (params == null) continue;
				for (PopulationParameter p : params) {
					if (p == null) continue;
					if (element.equals(p)) return cmt.getBlkId(); 
				}
				
				List<CovariateDefinition> covs = cmt.getListOfCovariate();
				if (covs == null) continue;
				for (CovariateDefinition cov : covs) {
					if (cov == null) continue;
					if (element.equals(cov)) return cmt.getBlkId();
					if (cov.getContinuous() != null) {
						List<CovariateTransformation> transformations = cov.getContinuous().getListOfTransformation();
						for (CovariateTransformation transformation : transformations) {
							if (transformation == null) continue;
							TransformedCovariate tc = transformation.getTransformedCovariate();
							if (tc == null) continue;
							if (element.equals(tc)) return cmt.getBlkId();
						}
					}
				}
			}						
		}
		
		return null;
	}
	
	/**
	 * Get a Block identifier hosting a model element.
	 * @param symbId Element identifier
	 * @return String or null
	 */
	public String getBlockId(String symbId) {
		if (symbId == null) return null;
		
		ModelDefinition md = dom.getModelDefinition();
		if (md == null) return null;
		
		List<VariabilityDefnBlock> vdbs = md.getListOfVariabilityModel();
		if (vdbs != null) {
			for (VariabilityDefnBlock vdb : vdbs) {
				if (vdb == null) continue;
				for (VariabilityLevelDefinition level : vdb.getLevel()) {
					if (level == null) continue;
					String currentSymbId = level.getSymbId();
					
					if (currentSymbId == null) continue;
					if (currentSymbId.equals(symbId)) return vdb.getBlkId();
				}
			}
		}
		
		for (CategoryRef_ category : categories) {
			if (category == null) continue;
			if (category.isSymbId(symbId)) return category.getBlkId(); 
		}
		
		List<ObservationModel> omts = md.getListOfObservationModel();
		if (omts != null) {
			for (ObservationModel omt : omts) {
				if (omt == null) continue;
				ContinuousObservationModel com = omt.getContinuousData();
				if (com != null) {
					for (Object o : com.getListOfObservationModelElement()) {
						if (isSymbol(o)) {
							Symbol s = (Symbol) o;
							if (symbId.equals(s.getSymbId())) return omt.getBlkId();
						}
					}
					
					ObservationError oe = com.getObservationError();
					if (oe != null) {
						String currentSymbolId = oe.getSymbId();
						if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return omt.getBlkId();
					}
				}
				
				Discrete discrete = omt.getDiscrete();
				if (discrete != null) {
					if (containsIdentifier(symbId, discrete.getCountData())) return omt.getBlkId();
					else if (containsIdentifer(symbId, discrete.getCategoricalData())) return omt.getBlkId();
					else if (containsIdentifer(symbId, discrete.getTimeToEventData())) return omt.getBlkId();
				}
			}
		}
		
		List<ParameterModel> pmts = md.getListOfParameterModel();
		if (pmts != null) {
			for (ParameterModel pmt : pmts) {
				if (pmt == null) continue;	
				for (PharmMLElement o : pmt.getListOfParameterModelElements()) {
					if (isSymbol(o)) {
						Symbol s = (Symbol) o;
						String currentSymbolId = s.getSymbId();
						if (symbId.equals(currentSymbolId)) return pmt.getBlkId();
					}
				}
			}
		}
		
		List<StructuralModel> smts = md.getListOfStructuralModel();
		if (smts != null) {
			for (StructuralModel smt : smts) {
				if (smt == null) continue;	
				for (PharmMLElement o : smt.getListOfStructuralModelElements()) {
					if (isSymbol(o)) {
						Symbol s = (Symbol) o;
						String currentSymbolId = s.getSymbId();
						if (symbId.equals(currentSymbolId)) return smt.getBlkId();
					}
				}
			}
		}
		
		// Search for parameters in the covariate model.
		List<CovariateModel> cmts = md.getListOfCovariateModel();
		if (cmts != null) {
			for (CovariateModel cmt : cmts) {
				if (cmt == null) continue;
				List<PopulationParameter> params = cmt.getListOfPopulationParameter();
				if (params == null) continue;
				for (PopulationParameter param : params) {
					if (param == null) continue;
					String currentSymbolId = param.getSymbId();
					if (currentSymbolId == null) continue;
					if (symbId.equals(currentSymbolId)) return cmt.getBlkId();
				}
				
				List<CovariateDefinition> covs = cmt.getListOfCovariate();
				if (covs == null) continue;
				for (CovariateDefinition cov : covs) {
					if (cov == null) continue;
					String currentSymbId = cov.getSymbId();
					if (currentSymbId == null) continue;
					if (symbId.equals(currentSymbId)) return cmt.getBlkId();
					
					if (cov.getContinuous() != null) {
						List<CovariateTransformation> transformations = cov.getContinuous().getListOfTransformation();
						for (CovariateTransformation transformation : transformations) {
							if (transformation == null) continue;
							TransformedCovariate tc = transformation.getTransformedCovariate();
							if (tc == null) continue;
							currentSymbId = tc.getSymbId();
							if (currentSymbId == null) continue;
							if (symbId.equals(currentSymbId)) return cmt.getBlkId();
						}
					}
					
				}
			}						
		}
		
		return null;
	}
	
	/**
	 * Get the category map used by an accessor instance.
	 * @return Map<PharmMLRootType, List<CategoryRef>>
	 */
	public Map<PharmMLRootType, List<CategoryRef_>> getCategoryMap() { return category_map; }
	
	/**
	 * Get the model handle currently bound to the accessor.
	 * @return eu.ddmore.libpharmml.dom.PharmML
	 */
	public PharmML getDom() { return dom; }
	
	/**
	 * Get a function definition by name.
	 * @param name Function name
	 * @return FunctionDefinition
	 */
	public FunctionDefinition getFunctionDefinition(String name) {
		FunctionDefinition f = null;
		
		if (name != null) {
			List<FunctionDefinition> funcs = dom.getListOfFunctionDefinition();
			if (funcs != null) {
				for (FunctionDefinition func : funcs) {
					if (func == null) continue;
					if (func.getSymbId() != null) {
						if (func.getSymbId().equals(name)) {
							f = func;
							break;
						}
					}
				}
			}
		}
		
		return f;
	}
	
	/**
	 * Get the independent variable registered with the PharmML model.
	 * @return IndependentVariable
	 */
	public IndependentVariable getIndependentVariable() {
		IndependentVariable iv = null;
		
		List<IndependentVariable> ivs = dom.getListOfIndependentVariable();
		if (ivs != null) {
			if (ivs.size() > 0) iv = ivs.get(0);
		}
		
		return iv;
	}
	
	/**
	 * Get the PK Macro list bound to a structural model.
	 * @param sm
	 * @return PKMacroList
	 */
	public PKMacroList getPKMacros(StructuralModel sm) {
		if (sm != null) {
			for (Object o : sm.getListOfStructuralModelElements()) {
				if (isPKMacroList(o)) return (PKMacroList) o;
			}
		}
		
		return null;
	}
	
	/**
	 * Check if a transformed covariate has an implementation expression.
	 * Otherwise the value needs to be read from an objective data file.
	 * @param tcov Transformed Covariate
	 * @return boolean
	 */
	public boolean hasAssignment(TransformedCovariate tcov) {
		if (tcov == null) return false;
		
		ModelDefinition md = dom.getModelDefinition();
		if (md == null) return false;
		
		List<CovariateModel> cmts = md.getListOfCovariateModel();
		if (cmts == null) return false;
		
		for (CovariateModel cmt : cmts) {
			if (cmt == null) continue;
			for (CovariateDefinition cov : cmt.getListOfCovariate()) {
				if (cov == null) continue;
				
				ContinuousCovariate ccov = cov.getContinuous(); 
				if (ccov == null) continue;
				
				for (CovariateTransformation transform : ccov.getListOfTransformation()) {
					if (transform == null) continue;
					if (tcov.equals(transform.getTransformedCovariate())) return transform.getAssign() != null;
				}
			}
		}
		
		return false;
	}
	
	private Administration oidSearchAdministrations(String oid, TrialDesign td) {
		if (oid == null || td == null) return null;
		
		Interventions ints = td.getInterventions();
		if (ints == null) return null;
		
		List<Administration> admins = ints.getListOfAdministration();
		if (admins == null) return null;
		if (admins.isEmpty()) return null;
		
		for (Administration o : admins) {
			if (o == null) continue;
			if (equals_(oid, o.getOid())) return o;
		}
		
		return null;
	}
	
	private ArmDefinition oidSearchArms(String oid, TrialDesign td) {
		if (oid == null || td == null) return null;
		
		Arms arms = td.getArms();
		if (arms == null) return null;
		
		List<ArmDefinition> list = arms.getListOfArm();
		if (list == null) return null;
		if (list.isEmpty()) return null;
		
		for (ArmDefinition o : list) {
			if (o == null) continue;
			if (equals_(oid, o.getOid())) return o;
		}
		
		return null;
	}
	
	private PharmMLRootType oidSearchObservations(String oid, TrialDesign td) {
		if (oid == null || td == null) return null;
		
		Observations obs = td.getObservations();
		if (obs == null) return null;
		
		for (PharmMLRootType o : obs.getListOfObservationsElements()) {
			if (isPharmMLObject(o)) {
				PharmMLObject obj = (PharmMLObject) o;
				if (equals_(oid, obj.getOid())) return o;
			}
		}
		
		List<OccasionSequence> occ_seqs = td.getListOfOccasions();
		for (OccasionSequence occ_seq : occ_seqs) {
			if (occ_seq == null) continue;
			OccasionList lo = occ_seq.getOccasionList();
			if (lo == null) continue;
			if (equals_(oid, lo.getOid())) return lo;
			
			List<Occasion> occasions = lo.getListOfOccasion();
			for (Occasion occasion : occasions) {
				if (occasion == null) continue;
				if (equals_(oid, occasion.getOid())) return occasion;
			}
		}
		
		return null;
	}
	
	private PharmMLRootType oidSearchTrialDesign(String oid, TrialDesign td) {
		if (oid == null || td == null) return null;
		
		PharmMLRootType element = null;
		if ((element = oidSearchAdministrations(oid, td)) != null) return element;
		if ((element = oidSearchObservations(oid, td)) != null) return element;
		if ((element = oidSearchArms(oid, td)) != null) return element;
		
		return null;
	}
	
	/**
	 * Register a category reference with the Acccessor class.
	 * @param ref Category Reference.
	 * @return boolean
	 */
	public boolean register(CategoryRef_ ref) {
		if (ref != null) {
			if (!categories.contains(ref)) {
				categories.add(ref);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Register a matrix declaration with a named variable.<br/>
	 * Allows a code generator to get a variable name that is assigned to a matrix type.<br/>
	 * The matrix type itself has no direct symbol referencing bound to the PharmML class.
	 * @param mv Matrix Declaration
	 * @return boolean 
	 */
	public boolean register(MatrixDeclaration mv) {
		if (mv != null) {
			if (!matrices.contains(mv)) {
				matrices.add(mv);
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Register a category list associated with an model element.
	 * @param element Model element
	 * @param list Category List
	 * @return boolean
	 */
	public boolean register(PharmMLRootType element, List<CategoryRef_> list) {
		if (element != null && list != null) {
			if (list.isEmpty()) return false;
			if (!category_map.containsKey(element)) {
				category_map.put(element, list);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Register machine generated model elements with a unique, randomly generated identifier.
	 * @param symbId Symbol Identifier
	 * @param o Model Element.
	 */
	public void register(String symbId, PharmMLRootType o) {
		if (symbId == null) throw new NullPointerException("Symbol is NULL.");
		if (o == null) throw new NullPointerException("The source object is NULL");
		if (machine_generated_elements.containsKey(symbId)) throw new IllegalStateException("A symbol identifier is not unique.");
		machine_generated_elements.put(symbId, o);
	}
	
	private PharmMLRootType search(String symbId, CategoricalData cd) {
		if (cd == null || symbId == null) return null;
		
		List<CommonDiscreteVariable> categories = cd.getListOfCategories();
		if (categories != null) {
			for (CommonDiscreteVariable category : categories) {
				if (category == null) continue;
				String currentSymbolId = category.getSymbId();
				if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return category;
			}
		}
		
		List<CommonDiscreteVariable> initial_states = cd.getListOfInitialStateVariable();
		if (initial_states != null) {
			for (CommonDiscreteVariable initial_state : initial_states) {
				if (initial_state == null) continue;
				String currentSymbolId = initial_state.getSymbId();
				if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return initial_state;
			}
		}
		
		List<CommonDiscreteVariable> previous_states = cd.getListOfPreviousStateVariable();
		if (previous_states != null) {
			for (CommonDiscreteVariable previous_state : previous_states) {
				if (previous_state == null) continue;
				String currentSymbolId = previous_state.getSymbId();
				if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return previous_state;
			}
		}
		
		return null;
	}
	
	private PharmMLRootType search(String symbId, CountData cd) {
		if (cd == null || symbId == null) return null;
		CommonDiscreteVariable cc = cd.getCountVariable();
		if (cc != null) {
			String currentSymbolId = cc.getSymbId();
			if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return cc;
		}
		
			
		DiscreteDataParameter dp = cd.getDispersionParameter();
		if (dp != null) {
			String currentSymbolId = dp.getSymbId();
			if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return dp;
		}
				
		List<DiscreteDataParameter> ips = cd.getListOfIntensityParameter();
		if (ips != null) {
			for (DiscreteDataParameter ip : ips) {
				if (ip == null) continue;
				String currentSymbolId = ip.getSymbId();
				if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return ip;
			}
		}
			
		List<CommonDiscreteVariable> pcvs = cd.getListOfPreviousCountVariable();
		if (ips != null) {
			for (CommonDiscreteVariable pcv : pcvs) {
				if (pcv == null) continue;
				String currentSymbolId = pcv.getSymbId();
				if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return pcv;
			}
		}
			
		DiscreteDataParameter mpp = cd.getMixtureProbabilityParameter();
		if (mpp != null) {
			String currentSymbolId = mpp.getSymbId();
			if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return mpp;
		}
			
		DiscreteDataParameter odp = cd.getOverDispersionParameter();
		if (odp != null) {
			String currentSymbolId = odp.getSymbId();
			if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return odp;
		}
		
		return null;
	}
	
	private PharmMLRootType search(String symbId, TimeToEventData tte) {
		if (tte == null || symbId == null) return null;
		
		CommonDiscreteVariable ev = tte.getEventVariable();
		if (ev != null) {
			String currentSymbolId = ev.getSymbId();
			if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return ev;
		}
				
		List<TTEFunction> hazard_funcs = tte.getListOfHazardFunction();
		if (hazard_funcs != null) {
			for (TTEFunction hazard_func : hazard_funcs) {
				if (hazard_func == null) continue;
				String currentSymbolId = hazard_func.getSymbId();
				if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return hazard_func;
			}
		}
		
		List<TTEFunction> survival_funcs = tte.getListOfSurvivalFunction();
		if (hazard_funcs != null) {
			for (TTEFunction survival_func : survival_funcs) {
				if (survival_func == null) continue;
				String currentSymbolId = survival_func.getSymbId();
				if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return survival_func;
			}
		}
	 	
		return null;
	}

	private PopulationParameter search_list5(String symbId, List<PopulationParameter> ps) {
		if (symbId == null || ps == null) return null;
		if (ps.isEmpty()) return null;
		
		for (PopulationParameter p : ps) {
			if (p == null) continue;
			String currentSymbolId = p.getSymbId();
			if (currentSymbolId == null) continue;
			if (symbId.equals(currentSymbolId)) return p;
		}
		
		return null;
	}
	
	private CategoryRef_ searchCategoryRefs(String symbId) {
		if (symbId == null) return null;
		for (CategoryRef_ category : categories) {
			if (category == null) continue;
			if (category.isSymbId(symbId)) return category; 
		}
		return null;
	}
	
	private PharmMLRootType searchCovariateModel(ModelDefinition md, String symbId) {
		if (symbId == null || md == null) return null;
		
		List<CovariateModel> cmts = md.getListOfCovariateModel();
		if (cmts == null) return null;
		
		for (CovariateModel cmt : cmts) {
			if (cmt == null) continue;
				
			PharmMLRootType element = search_list5(symbId, cmt.getListOfPopulationParameter());
			if (element != null) return element;
			
			List<CovariateDefinition> covs = cmt.getListOfCovariate();
			if (covs == null) continue;
			for (CovariateDefinition cov : covs) {
				if (cov == null) continue;
				String currentSymbolId = cov.getSymbId();
				if (currentSymbolId == null) continue;
				if (symbId.equals(currentSymbolId)) return cov;			
						
				if (cov.getContinuous() != null) {
					List<CovariateTransformation> transformations = cov.getContinuous().getListOfTransformation();
					for (CovariateTransformation transformation : transformations) {
						if (transformation == null) continue;
						
						TransformedCovariate tc = transformation.getTransformedCovariate();
						if (tc == null) continue;
						
						currentSymbolId = tc.getSymbId();
						if (symbId.equals(currentSymbolId)) return tc;
							
					}
				}
			}
		} 
		
		return null;
	}
	
	private PharmMLRootType searchCovariateModel(ModelDefinition md, String symbId, String blkId) {
		if (symbId == null || blkId == null || md == null) return null;
		
		List<CovariateModel> cmts = md.getListOfCovariateModel();
		if (cmts == null) return null;
		
		for (CovariateModel cmt : cmts) {
			if (cmt == null) continue;
		
			String currentBlkId = cmt.getBlkId();
			if (!blkId.equals(currentBlkId)) continue;
			
			PharmMLRootType element = search_list5(symbId, cmt.getListOfPopulationParameter());
			if (element != null) return element;
			
			List<CovariateDefinition> covs = cmt.getListOfCovariate();
			if (covs == null) continue;
			for (CovariateDefinition cov : covs) {
				if (cov == null) continue;
				String currentSymbolId = cov.getSymbId();
				if (currentSymbolId == null) continue;
				if (symbId.equals(currentSymbolId)) return cov;			
						
				if (cov.getContinuous() != null) {
					List<CovariateTransformation> transformations = cov.getContinuous().getListOfTransformation();
					for (CovariateTransformation transformation : transformations) {
						if (transformation == null) continue;
						
						TransformedCovariate tc = transformation.getTransformedCovariate();
						if (tc == null) continue;
						
						currentSymbolId = tc.getSymbId();
						if (symbId.equals(currentSymbolId)) return tc;
							
					}
				}
			}
		} 
		
		return null;
	}
	
	private MatrixDeclaration searchMatrixDeclarations(String symbId) {
		if (symbId == null) return null;
		
		for (MatrixDeclaration matrix : matrices) {
			if (matrix == null) continue;
			SymbolRef ref = matrix.ref;
			if (ref == null) continue;
			String currentSymbolId = ref.getSymbIdRef();
			if (symbId.equals(currentSymbolId)) return matrix;
		}
		
		return null;
	}
	
	private MatrixDeclaration searchMatrixDeclarations(String blkId, String symbId) {
		if (blkId == null || symbId == null) return null;
		
		for (MatrixDeclaration matrix : matrices) {
			if (matrix == null) continue;
			SymbolRef mref = matrix.ref;
			if (mref == null) continue;
			String currentBlkId = mref.getBlkIdRef();
			String currentSymbolId = mref.getSymbIdRef();
			if (currentBlkId == null) continue;
			if (currentBlkId.equals(blkId) && symbId.equals(currentSymbolId)) return matrix;
		}
		
		return null;
	}
	
	private PharmMLRootType searchObservationModel(ModelDefinition md, String symbId) {
		if (symbId == null || md == null) return null;

		List<ObservationModel> omts = md.getListOfObservationModel();
		if (omts == null) return null; 
		if (omts.isEmpty()) return null;

		for (ObservationModel omt : omts) {
			if (omt == null) continue;

			ContinuousObservationModel com = omt.getContinuousData();
			if (com != null) {
				for (Object o : com.getListOfObservationModelElement()) {
					if (isSymbol(o)) {
						Symbol s = (Symbol) o;
						if (symbId.equals(s.getSymbId())) return (PharmMLRootType) o;
					}
				}
				
				ObservationError oe = com.getObservationError();
				if (oe != null) {
					String currentSymbolId = oe.getSymbId();
					if (symbId.equals(currentSymbolId)) return oe;
				}
			}

			// Discrete observation model
			Discrete discrete = omt.getDiscrete();
			if (discrete != null) {
				CountData cd = discrete.getCountData();
				PharmMLRootType element = search(symbId, cd);
				if (element != null) return element;

				CategoricalData cat_data = discrete.getCategoricalData();
				element = search(symbId, cat_data);
				if (element != null) return element;

				TimeToEventData tte_data = discrete.getTimeToEventData();
				element = search(symbId, tte_data);
				if (element != null) return element;
			} 		
		}

		return null;
	}
	
	private PharmMLRootType searchObservationModel(ModelDefinition md, String symbId, String blkId) {
		if (symbId == null || md == null) return null;

		List<ObservationModel> omts = md.getListOfObservationModel();
		if (omts == null) return null; 
		if (omts.isEmpty()) return null;

		for (ObservationModel omt : omts) {
			if (omt == null) continue;
			String currentBlkId = omt.getBlkId();

			if (currentBlkId == null) continue;
			if (blkId.equals(currentBlkId)) {
				ContinuousObservationModel com = omt.getContinuousData();
				if (com != null) {
					for (Object o : com.getListOfObservationModelElement()) {
						if (isSymbol(o)) {
							Symbol s = (Symbol) o;
							if (symbId.equals(s.getSymbId())) return (PharmMLRootType) o;
						}
					}
					
					ObservationError oe = com.getObservationError();
					if (oe != null) {
						String currentSymbolId = oe.getSymbId();
						if (currentSymbolId != null) if (symbId.equals(currentSymbolId)) return oe;
					}		
				}

				// Discrete observation model
				Discrete discrete = omt.getDiscrete();
				if (discrete != null) {
					CountData cd = discrete.getCountData();
					PharmMLRootType element = search(symbId, cd);
					if (element != null) return element;

					CategoricalData cat_data = discrete.getCategoricalData();
					element = search(symbId, cat_data);
					if (element != null) return element;

					TimeToEventData tte_data = discrete.getTimeToEventData();
					element = search(symbId, tte_data);
					if (element != null) return element;
				}

			}				
		}

		return null;
	}
	
	private PharmMLRootType searchParameterModel(ModelDefinition md, String symbId) {
		if (symbId == null || md == null) return null;
		
		List<ParameterModel> pmts = md.getListOfParameterModel();
		if (pmts == null) return null; 
			
		for (ParameterModel pmt : pmts) {
			if (pmt == null) continue;	
			for (PharmMLElement element : pmt.getListOfParameterModelElements()) {
				if (isSymbol(element)) {
					Symbol symbol = (Symbol) element; 
					if (symbId.equals(symbol.getSymbId()) && isRootType(element)) {
						return (PharmMLRootType) element;
					}
				}
			}
		}
		
		return null;
	}
	
	private PharmMLRootType searchParameterModel(ModelDefinition md, String symbId, String blkId) {
		if (symbId == null || blkId == null || md == null) return null;

		List<ParameterModel> pmts = md.getListOfParameterModel();
		if (pmts == null) return null;

		for (ParameterModel pmt : pmts) {
			if (pmt == null) continue;

			String currentBlkId = pmt.getBlkId();
			if (blkId.equals(currentBlkId)) {
				for (PharmMLElement element : pmt.getListOfParameterModelElements()) {
					if (isSymbol(element)) {
						Symbol symbol = (Symbol) element; 
						if (symbId.equals(symbol.getSymbId()) && isRootType(element)) {
							return (PharmMLRootType) element;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	private PharmMLRootType searchStructuralModel(ModelDefinition md, String symbId) {
		if (symbId == null || md == null) return null;
		
		List<StructuralModel> smts = md.getListOfStructuralModel();
		if (smts == null) return null;
		
		for (StructuralModel smt : smts) {
			if (smt == null) continue;
			for (PharmMLElement element : smt.getListOfStructuralModelElements()) {
				if (isSymbol(element)) {
					Symbol symbol = (Symbol) element; 
					if (symbId.equals(symbol.getSymbId()) && isRootType(element)) {
						return (PharmMLRootType) element;
					}
				}
			}
		}
		
		return null;
	}
	
	private PharmMLRootType searchStructuralModel(ModelDefinition md, String symbId, String blkId) {
		
		if (symbId == null || blkId == null || md == null) return null;
		
		List<StructuralModel> smts = md.getListOfStructuralModel();
		if (smts == null) return null;
		
		for (StructuralModel smt : smts) {
			if (smt == null) continue;
			
			String currentBlkId = smt.getBlkId();
			if (currentBlkId == null) continue;
			
			if (blkId.equals(currentBlkId)) {
				for (PharmMLElement element : smt.getListOfStructuralModelElements()) {
					if (isSymbol(element)) {
						Symbol symbol = (Symbol) element; 
						if (symbId.equals(symbol.getSymbId()) && isRootType(element)) {
							return (PharmMLRootType) element;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	private VariabilityLevelDefinition searchVariabilityModel(ModelDefinition md, String symbId) {
		if (symbId == null || md == null) return null;
		
		List<VariabilityDefnBlock> vdbs = md.getListOfVariabilityModel();
		if (vdbs == null)  return null;
		
		for (VariabilityDefnBlock vdb : vdbs) {
			if (vdb == null) continue;
			for (VariabilityLevelDefinition level : vdb.getLevel()) {
				if (level == null) continue;
				if (symbId.equals(level.getSymbId())) return level;
			}
		}
		
		return null;
	}
	
	private PharmMLRootType searchVariabilityModel(ModelDefinition md, String symbId, String blkId) {
		if (symbId == null || blkId == null || md == null) return null;
		
		List<VariabilityDefnBlock> vdbs = md.getListOfVariabilityModel();
		if (vdbs == null)  return null;
		
		for (VariabilityDefnBlock vdb : vdbs) {
			if (vdb == null) continue;
			String currentBlkId = vdb.getBlkId();
			if (currentBlkId == null) continue;
			if (blkId.equals(currentBlkId)) {
				for (VariabilityLevelDefinition level : vdb.getLevel()) {
					if (level == null) continue;
					if (symbId.equals(level.getSymbId())) return level; 
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Set a block scope for to restrict variable searching to a local code block.
	 * Required as UncertML variable references supplies only a variable name
	 * and not a code block identifier.
	 * 
	 * @param blkId
	 * @see eu.ddmore.libpharmml.dom.uncertml.VarRefType
	 */
	public void setBlockScope(String blkId) { currentblkId = blkId; }
	
	/**
	 * Set the PharmML model of the Accessor
	 * @param dom_ PharmmL model
	 */
	public void setDom(PharmML dom_) { dom = dom_; }
}
