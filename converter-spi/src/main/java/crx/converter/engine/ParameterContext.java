/*******************************************************************************
 * Copyright (C) 2015 Cyprotex Discovery Ltd - All rights reserved.
 ******************************************************************************/

package crx.converter.engine;

import eu.ddmore.libpharmml.dom.modeldefn.PopulationParameter;
import eu.ddmore.libpharmml.dom.modellingsteps.ParameterEstimate;

/**
 * Convenience class to create metadata for parameter usage.<br/>
 * Required for language generation for MDL/TEL and MLXTrans.
 */
public class ParameterContext {
	/**
	 * The usage of a parameter in a PharmML model.
	 */
	public static enum Usage {
		/**
		 * Probability term in a univariate bernoulli distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.BernoulliDistributionType#getCategoryProb()
		 */
		CATEEGORICAL_UNIVARIATE_BERNOULLI,
		
		/**
		 * Probability term in a categorical distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.CategoricalDistributionType#getCategoryProb()
		 */
		CATEGORICAL_DISTRIBUTION_PROBABILITY,
		
		/**
		 * Number of trials term in a multinomial distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.MultinomialDistributionType#getNumberOfTrials()
		 */
		DISCRETE_MULTIVARIATE_MULTINOMIAL_NTRIALS,
		
		/**
		 * Probability term in a multinomial distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.MultinomialDistributionType#getProbabilities()
		 */
		DISCRETE_MULTIVARIATE_MULTINOMIAL_PROBABILITY,
		
		/**
		 * Degree of freedom term in a Wishart Distribution
		 * @see eu.ddmore.libpharmml.dom.uncertml.WishartDistributionType#getDegreesOfFreedom()
		 */
		DISCRETE_MULTIVARIATE_WISHART_DOF,
		
		/**
		 * Degree of freedom term in a Wishart Distribution
		 * @see eu.ddmore.libpharmml.dom.uncertml.WishartDistributionType#getScaleMatrix()
		 */
		DISCRETE_MULTIVARIATE_WISHART_SCALEMATRIX,
		
		/**
		 * Number of trials of a Binomial Distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.BinomialDistribution#getNumberOfTrials()
		 */
		DISCRETE_UNIVARIATE_BINOMIAL_NTRIALS,
		
		/**
		 * Probability Of Success for a Binomial Distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.BinomialDistribution#getProbabilityOfSuccess()
		 */
		DISCRETE_UNIVARIATE_BINOMIAL_PROBAILITY_OF_SUCCESSES,
		
		/**
		 * Probability of successes for a Geometric Distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.GeometricDistributionType#getProbability()
		 */
		DISCRETE_UNIVARIATE_GEOMETRIC_PROBABILTY,
		
		/**
		 * Number Of successes for a Hypergeometric Distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.HypergeometricDistributionType#getNumberOfSuccesses()
		 */
		DISCRETE_UNIVARIATE_HYPERGEOMETRIC_NSUCCESSES,
		
		/**
		 * Number of trials for a Hypergeometric Distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.HypergeometricDistributionType#getNumberOfTrials()
		 */
		DISCRETE_UNIVARIATE_HYPERGEOMETRIC_NTRIALS,
		
		/**
		 * Population Size for a Hypergeometric Distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.HypergeometricDistributionType#getPopulationSize()
		 */
		DISCRETE_UNIVARIATE_HYPERGEOMETRIC_POPULATION_SIZE,
		
		/**
		 * Number of failures for a Negative Binomial Distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.NegativeBinomialDistributionType#getNumberOfFailures()
		 */
		DISCRETE_UNIVARIATE_NEGATIVEBINOMIAL_NFAILURES,
		
		/**
		 * Probability for a Negative Binomial Distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.NegativeBinomialDistributionType#getProbability()
		 */
		DISCRETE_UNIVARIATE_NEGATIVEBINOMIAL_PROBAILITY,
		
		/**
		 * Probability for a Poisson Distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.PoissonDistributionType#getRate()
		 */
		DISCRETE_UNIVARIATE_POISSON_RATE,
		
		/**
		 * Alpha variable of a beta distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.BetaDistributionType#getAlpha()
		 */
		UNIVARIATE_BETA_ALPHA,
		
		/**
		 * Beta variable  of a beta distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.BetaDistributionType#getBeta()
		 */
		UNIVARIATE_BETA_BETA,
		
		/**
		 * Location variable in a cauchy distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.CauchyDistribution#getLocation()
		 */
		UNIVARIATE_CAUCHY_LOCATION,
		
		/**
		 * Location variable in a cauchy distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.CauchyDistribution#getScale()
		 */
		UNIVARIATE_CAUCHY_SCALE,
		
		/**
		 * Degrees of freedom in a Chi-squared distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.ChiSquareDistribution#getDegreesOfFreedom()
		 */
		UNIVARIATE_CHISQUARED_DOF,
		
		/**
		 * Rate parameter in an exponential distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.ExponentialDistribution#getRate() 
		 */
		UNIVARIATE_EXPONENTIAL_RATE,
		
		/**
		 * Denominator of a F-distribution
		 * @see eu.ddmore.libpharmml.dom.uncertml.FDistribution#getDenominator()
		 */
		UNIVARIATE_F_DENOMINATOR,
		
		/**
		 * Numerator of a F-distribution
		 * @see eu.ddmore.libpharmml.dom.uncertml.FDistribution#getNumerator()
		 */
		UNIVARIATE_F_NUMERATOR,
		
		/**
		 * Scale parameter of a gamma distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.GammaDistribution#getScale()
		 */
		UNIVARIATE_GAMMA_SCALE,
		
		/**
		 * Shape parameter of a gamma distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.GammaDistribution#getShape()
		 */
		UNIVARIATE_GAMMA_SHAPE,
		
		/**
		 * Scale parameter of an inverse gamma distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.InverseGammaDistribution#getScale()
		 */
		UNIVARIATE_INVERSEGAMMA_SCALE,
		
		/**
		 * Shape parameter of an inverse gamma distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.InverseGammaDistribution#getShape()
		 */
		UNIVARIATE_INVERSEGAMMA_SHAPE,
		
		/**
		 * Location parameter of an laplace distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.LaplaceDistribution#getLocation()
		 */
		UNIVARIATE_LAPLACE_LOCATION,
		
		/**
		 * Scale parameter of an laplace distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.LaplaceDistribution#getScale()
		 */
		UNIVARIATE_LAPLACE_SCALE,
		
		/**
		 * Location parameter in a logistic distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.LogisticDistribution#getLocation()
		 */
		UNIVARIATE_LOGISTIC_LOCATION,
		
		/**
		 * Scale parameter in a logistic distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.LogisticDistribution#getScale()
		 */
		UNIVARIATE_LOGISTIC_SCALE,
		
		/**
		 * Logscale parameter in a lognormal distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.LogNormalDistribution#getLogScale()
		 */
		UNIVARIATE_LOGNORMAL_LOGSCALE,
		
		/**
		 * Shape parameter in a lognormal distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.LogNormalDistribution#getShape()
		 */
		UNIVARIATE_LOGNORMAL_SHAPE,
		
		/**
		 * Mean of a normal distribution
		 * @see eu.ddmore.libpharmml.dom.uncertml.NormalDistribution#getMean()
		 */
		UNIVARIATE_NORMAL_MEAN,
		
		/**
		 * Standard deviation of a normal distribution
		 * @see eu.ddmore.libpharmml.dom.uncertml.NormalDistribution#getStddev()
		 */
		UNIVARIATE_NORMAL_STDDEV,
		
		/**
		 * Variance deviation of a normal distribution
		 * @see eu.ddmore.libpharmml.dom.uncertml.NormalDistribution#getStddev()
		 */
		UNIVARIATE_NORMAL_VARIANCE,
		
		/**
		 * Scale value of a Pareto distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.ParetoDistribution#getScale()
		 */
		UNIVARIATE_PARETO_SCALE,
		
		/**
		 * Shape value of a Pareto distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.ParetoDistribution#getShape()
		 */
		UNIVARIATE_PARETO_SHAPE,
		
		/**
		 * Degrees of freedom in a student-t distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.StudentTDistribution#getDegreesOfFreedom() 
		 */
		UNIVARIATE_STUDENT_DOF,
		
		/**
		 * Degrees of freedom in a student-t distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.StudentTDistribution#getLocation()
		 */
		UNIVARIATE_STUDENT_LOCATION,
		
		/**
		 * Scale parameter of a student-t distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.StudentTDistribution#getScale()
		 */
		UNIVARIATE_STUDENT_SCALE,
		
		/**
		 * Max value of a uniform distribution
		 * @see eu.ddmore.libpharmml.dom.uncertml.UniformDistribution#getMaximum()
		 */
		UNIVARIATE_UNIFORM_MAX,
		
		/**
		 * Min value of a uniform distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.UniformDistribution#getMinimum()
		 */
		UNIVARIATE_UNIFORM_MIN,
		
		/**
		 * Number of classes in a uniform distribution.
		 * @see eu.ddmore.libpharmml.dom.uncertml.UniformDistribution#getMinimum()
		 */
		UNIVARIATE_UNIFORM_NCLASSES,
		
		/**
		 * Scale parameter in a Weibull distribution
		 * @see eu.ddmore.libpharmml.dom.uncertml.WeibullDistribution#getScale()
		 */
		UNIVARIATE_WEIBULL_SCALE,
		
		/**
		 * Scale parameter in a Weibull distribution
		 * @see eu.ddmore.libpharmml.dom.uncertml.WeibullDistribution#getShape()
		 */
		UNIVARIATE_WEIBULL_SHAPE,
		
		/**
		 * Unknown context.
		 */
		UNKNOWN
	}
	
	/**
	 * A parameter is active in a correlation calculation.
	 */
	public boolean correlation = false;
	
	/**
	 * A parameter is active in a error model.
	 */
	public boolean error_model = false;
	
	/**
	 * A fixed parameter in a estimation.
	 */
	public boolean fixed = false;
	
	/**
	 * Check to see if a parameter is used in a group scope.
	 * That's the parameter is used in a Individual parameter with a general covariate.
	 */
	public boolean group_scope = false;
	
	/**
	 * The parameter is a distribution mean
	 */
	public boolean mean = false;
	
	/**
	 * The parameter is part of a mixture model.
	 */
	public boolean mixture_model_scope = false;
	
	/**
	 * Parameter is a number of trials property for a discrete distribution.
	 */
	public boolean ntrials = false;
	
	/**
	 * Parameter is a number of failures property for a discrete distribution.
	 */
	public boolean numberOfFailures = false;
	
	/**
	 * Flag if parameter has a OMEGA context.
	 * This basically means a parameter is assigned to an ETA of a random effect.
	 */
	public boolean omega = false;
	
	/**
	 * Parameter that owns the context.
	 */
	public PopulationParameter p = null;
	
	/**
	 * Parameter estimate that owns the context.
	 */
	public ParameterEstimate pe = null;
	
	/**
	 * Parameter is a population size property in a discrete distribution.
	 */
	public boolean populationSize = false;
	
	/**
	 * Parameter used as a probability value in a discrete distribution function.
	 */
	public boolean probability = false;
	
	/**
	 * Parameter used as a probability of success property for a discrete distribution.
	 */
	public boolean probability_of_successes = false;
	
	/**
	 * Parameter is used as rate in a discrete distribution
	 */
	public boolean rate = false;
	
	/**
	 * Parameter is used as rate as residual in an error model.
	 */
	public boolean residual = false;
	
	/**
	 * Parameter is used as a STDDEV.
	 */
	public boolean stddev = false;
	
	/**
	 * The parameter has a structural model scope.
	 */
	public boolean structural_scope = true;
	
	/**
	 * Flag if parameter has a THETA context.
	 */
	public boolean theta = false;
	
	/**
	 * Flag if parameter is a fixed effect THETA context.
	 */
	public boolean theta_fixed_effect = false;
	
	/**
	 * Generic type for the Parameter usage.
	 */
	public Usage usage = Usage.UNKNOWN;
	
	/**
	 * Parameter acting as a variance value. 
	 */
	public boolean variance = false;
	
	/**
	 * Constructor
	 * @param p_ Source parameter for the context record
	 */
	public ParameterContext(PopulationParameter p_) {
		if (p_ == null) throw new NullPointerException("Parameter is NULL");
		p = p_;
	}
	
	@Override
	public String toString() { return p.getSymbId(); }
}