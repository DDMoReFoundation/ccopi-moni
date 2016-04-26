/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.spi.steps;

import java.util.List;

import crx.converter.engine.Part;
import crx.converter.engine.common.ConditionalDoseEvent;
import crx.converter.engine.common.MultipleDvRef;
import crx.converter.engine.common.TemporalDoseEvent;
import crx.converter.engine.common.BaseTabularDataset.ElementMapping;
import eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType;
import eu.ddmore.libpharmml.dom.commontypes.VariableDefinition;
import eu.ddmore.libpharmml.dom.dataset.ColumnDefinition;
import eu.ddmore.libpharmml.dom.dataset.ColumnType;
import eu.ddmore.libpharmml.dom.trialdesign.ExternalDataSet;


/**
 * Abstract/Base analysis step.
 * Methods mostly to process an external data set references and column mappings.
 */
public interface BaseStep extends Part {
	/**
	 * Default value for the tool name associated with the estimation step.
	 */
	public static final String UNKNOWN_TOOLNAME  = "unknown";
	
	/**
	 * Get the list of categorical dose events as mapped to a single column in 
	 * an external dataset.<br/>
	 * Basically this flags the NONMEM/Monolix dosing target in a data frame.
	 * @return List<ConditionalDoseEvent>
	 */
	public List<ConditionalDoseEvent> getCategoricalDoseEvents();
	
	/**
	 * Get a named column associated with an external data set.
	 * @param name Column Name
	 * @return eu.ddmore.libpharmml.dom.dataset.ColumnDefinition
	 */
	public ColumnDefinition getColumn(String name);
	
	/**
	 * Get the columns for a specific usage.
	 * Returns a list as dose possible for multiple compartments or multiple covariate columns.
	 * @param usage Usage
	 * @return java.util.List<ElementMapping>
	 */
	public List<ColumnDefinition> getColumns(ColumnType usage);
	
	/**
	 * Get the conditional dose events (if any) linked to the estimation.
	 * @return java.util.List<ConditionalDoseEvent>
	 */
	public List<ConditionalDoseEvent> getConditionalDoseEvents();
	
	/**
	 * Get the element mapping to the objective dataset.
	 * @return java.util.List<ElementMapping>
	 */
	public List<ElementMapping> getElementMappings();
	
	/**
	 * Get the element mapping to the objective dataset.
	 * @param usage Column Usage
	 * @return java.util.List<ElementMapping>
	 */
	public List<ElementMapping> getElementMappings(ColumnType usage);
	
	/**
	 * Return specific model elements associated with a column usage.
	 * @param usage
	 * @return java.util.List<eu.ddmore.libpharmml.dom.commontypes.PharmMLRootType>
	 */
	public List<PharmMLRootType> getElements(ColumnType usage);
	
	/**
	 * Get the External data set that contains the objectivve data file reference.
	 * @return eu.ddmore.libpharmml.dom.modellingsteps.ExternalDataSet
	 */
	public ExternalDataSet getExternalDataSet ();
	
	/**
	 * Get the ignore line/row character symbol for a row in a data frame.<br/>
	 * @return java.lang.String.
	 */
	public String getIgnoreLineSymbol();
	
	/**
	 * Get the column definition for the declared infusion column.
	 * @return ColumnDefinition
	 */
	public ColumnDefinition getInfusionColumn();
	
	/**
	 * Flag that the step has a multiple DV mappings references mapped to an external data file.
	 * This construct can be used to switch a DV column to different error models based on the value read from a CSV file.
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.trialdesign.MultipleDVMapping
	 * @see eu.ddmore.libpharmml.dom.trialdesign.ExternalDataSet#getListOfColumnMappingOrColumnTransformationOrMultipleDVMapping() 
	 */
	public List<MultipleDvRef> getMultipleDvRefs();
	
	/**
	 * Get Temporal Dose event as read from an external dataset.
	 * @return TemporalDoseEvent
	 */
	public TemporalDoseEvent getTemporalDoseEvent();
	
	/**
	 * Get the tool name associated with the estimation step.<br/>
	 * Read using the value referenced by an external dataset.
	 * @return java.lang.String
	 * @see eu.ddmore.libpharmml.dom.trialdesign.ExternalDataSet#getToolName()
	 */
	public String getToolName();
	
	/**
	 * Flag if estimation involves conditional dosing.
	 * @return boolean
	 * @see ConditionalDoseEvent
	 */
	public boolean hasConditionalDoseEvents();
	
	/**
	 * Flag that the step has a multiple DV mappings references mapped to an external data file.
	 * This construct can be used to switch a DV column to different error models based on the value read from a CSV file.
	 * @return boolean
	 * @see eu.ddmore.libpharmml.dom.trialdesign.MultipleDVMapping
	 * @see eu.ddmore.libpharmml.dom.trialdesign.ExternalDataSet#getListOfColumnMappingOrColumnTransformationOrMultipleDVMapping() 
	 */
	public boolean hasMultipleDVRefs();
	
	/**
	 * Flag whether the modelling the Temporal Dose event as read from an external dataset.
	 * @return boolean
	 */
	public boolean hasTemporalDoseEvent();
	
	/**
	 * Checks if a referenced data column in an external dataset is a categorical dose event.
	 * @param colName Input Column Name
	 * @return boolean
	 */
	public boolean isCategoricalDoseTargetColumn(String colName);
	
	/**
	 * Check if a local variable is a conditional dosing target variable.<br/>
	 * If so, then needs to be isolated in languages like NONMEM.
	 * @param v
	 * @return boolean
	 */
	public boolean isConditionalDoseEventTarget(VariableDefinition v);
	
	/**
	 * Flag is a step/task is undergoing an infusion process
	 * @return boolean
	 */
	public boolean isDoingInfusion();
}
