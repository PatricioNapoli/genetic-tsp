import java.util.ArrayList;
import java.util.Random;
import Base.*;

public class Main
	{
		private static SimulationData			_simulationData;			// Contiene informacion para el algoritmo genetico
		private static ArrayList<Chromosome>	_population;				// Poblacion
		private static ArrayList<Chromosome>	_selectedParents;			// Lista de individuos seleccionados para ser padres.
		private static ArrayList<Chromosome>	_children;					// Lista de individuos creados en la operacion de 'crossover'
		private static int						_currentGeneration	= 0;	// Lleva la cuenta de las generaciones.
		private static int						SegmentationSize	= 6;
		private static int						InitialPopulation	= 400;
		private static int						MutationProbability	= 5;

		public static void main(String[] args)
			{
				_simulationData = new SimulationData();
				createFirstGeneration(InitialPopulation);
				System.out.println(">>> Comienza la simulacion!!!");
				while (!solutionFound())
					{
						_currentGeneration++;
						int averageDistance = 0;
						for (Chromosome chromosome : _population)
							{
								averageDistance += _simulationData.calculateDistanceBetweenCities(chromosome);
							}
						System.out.println("~~~ GENERATION " + _currentGeneration + " AVERAGE DISTANCE: " + averageDistance / InitialPopulation + " ~~~");
						if (_currentGeneration > 1000000)
							{
								System.out.println(">>> ERROR: Pasaron 1.000.000 de generaciones y no se encontro la solucion...");
								break;
							}
						selection();
						crossover();
						mutation();
						updatePopulation();
					}
			}

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		public static void selection()
			{
				if (_selectedParents == null)
					_selectedParents = new ArrayList<Chromosome>();
				else
					_selectedParents.clear();
				ArrayList<Chromosome> sortedPopulation = new ArrayList<Chromosome>(_simulationData.sortItemsByFitness(_population).subList(0, InitialPopulation / 2));
				_selectedParents = _simulationData.getItemsByRoulletteWheelSelection(sortedPopulation, InitialPopulation / 4);
			}

		public static void crossover()
			{
				if (_children == null)
					_children = new ArrayList<Chromosome>();
				else
					_children.clear();
				for (int i = 0; i < _selectedParents.size() - 1; i++)
					{
						Chromosome mother = _selectedParents.get(i);
						Chromosome father = _selectedParents.get(i + 1);
						_children.add(createChild(father, mother));
						_children.add(createChild(mother, father));
					}
			}

		public static void mutation()
			{
				Random rnd = new Random();
				float mutationProbability = MutationProbability;
				for (Chromosome chromosome : _children)
					{
						if (rnd.nextInt(100) <= mutationProbability)
							{
								int segmentStart = rnd.nextInt(chromosome.genes.size() - SegmentationSize);
								int[] segmentation = getSegmentation(SegmentationSize, segmentStart, chromosome);
								for (int i = segmentation.length - 1; i > 0; i--)
									{
										int index = rnd.nextInt(i + 1);
										int a = segmentation[index];
										segmentation[index] = segmentation[i];
										segmentation[i] = a;
									}
								for (int i = 0; i < segmentation.length; i++)
									{
										chromosome.genes.set(segmentStart + i, segmentation[i]);
									}
							}
					}
			}

		public static void updatePopulation()
			{
				ArrayList<Chromosome> sortedPopulation = new ArrayList<Chromosome>(_simulationData.sortItemsByFitness(_population).subList(0, InitialPopulation / 2));
				sortedPopulation.addAll(_simulationData.sortItemsByFitness(_children));
				_population = new ArrayList<Chromosome>();
				_population.addAll(sortedPopulation);
			}

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		public static Chromosome createChild(Chromosome parentSegmentation, Chromosome parentCrossover)
			{
				Chromosome child = new Chromosome();
				child.genes.addAll(parentSegmentation.genes);
				Random rnd = new Random();
				int bigSegmentStart = rnd.nextInt(parentSegmentation.genes.size() - SegmentationSize);
				boolean[] mappedOk = new boolean[child.genes.size()];
				for (int j = bigSegmentStart; j < bigSegmentStart + SegmentationSize; j++)
					{
						mappedOk[j] = true;
					}
				for (int j = bigSegmentStart; j < bigSegmentStart + SegmentationSize; j++)
					{
						int numberToCheck = parentCrossover.genes.get(j);
						if (parentSegmentation.genes.indexOf(numberToCheck) >= bigSegmentStart && parentSegmentation.genes.indexOf(numberToCheck) < bigSegmentStart + SegmentationSize)
							{
								continue;
							}
						boolean mapped = false;
						int indexToMap = parentCrossover.genes.indexOf(parentSegmentation.genes.get(j));
						if (indexToMap < bigSegmentStart || indexToMap >= bigSegmentStart + SegmentationSize)
							{
								if (!mappedOk[indexToMap])
									{
										child.genes.set(indexToMap, parentCrossover.genes.get(j));
										mappedOk[indexToMap] = true;
									}
								mapped = true;
							}
						int alternativeIndex = indexToMap;
						while (!mapped)
							{
								alternativeIndex = parentCrossover.genes.indexOf(parentSegmentation.genes.get(alternativeIndex));
								if (alternativeIndex < bigSegmentStart || alternativeIndex >= bigSegmentStart + SegmentationSize)
									{
										if (!mappedOk[alternativeIndex])
											{
												child.genes.set(alternativeIndex, parentCrossover.genes.get(j));
												mappedOk[alternativeIndex] = true;
											}
										mapped = true;
									}
							}
					}
				for (int j = 0; j < mappedOk.length; j++)
					{
						if (!mappedOk[j])
							{
								child.genes.set(j, parentCrossover.genes.get(j));
							}
					}
				return child;
			}

		public static int[] getSegmentation(int size, int segmentStart, Chromosome chromosome)
			{
				int[] segmentation = new int[size];
				for (int i = 0; i < segmentation.length; i++)
					{
						segmentation[i] = chromosome.genes.get(segmentStart + i);
					}
				return segmentation;
			}

		private static void createFirstGeneration(int populationSize)
			{
				_population = new ArrayList<Chromosome>();
				int amountOfCities = _simulationData.getAmountOfCities();
				Random rnd = new Random();
				for (int i = 0; i < populationSize; i++)
					{
						// Inicializamos al individuo
						Chromosome chromosome = new Chromosome();
						// Seteamos randomicamente los valores de cada gen.
						for (int j = 0; j < amountOfCities; j++)
							{
								// Obtenemos un valor aleatorio
								int possibleGen = rnd.nextInt(amountOfCities);
								// Si el cromosoma ya contiene este valor, volvemos a obtener un valor aleatorio
								while (chromosome.genes.contains(possibleGen))
									{
										possibleGen = rnd.nextInt(amountOfCities);
									}
								// Una vez que nos garantizamos que este valor no esta en el cromosoma, lo agregamos.
								chromosome.genes.add(possibleGen);
							}
						// Agregamos al individuo a la poblacion.
						_population.add(chromosome);
					}
			}

		private static boolean solutionFound()
			{
				boolean solutionFound = false;
				int solutionIndex = -1;
				for (int i = 0; i < _population.size(); i++)
					{
						// Si el fitness de este individuo es mejor o igual a la solucion 'ideal'
						// Guardamos el indice de la solucion.
						if (_simulationData.calculateFitness(_population.get(i)) >= _simulationData.bestApproximateFitness())
							{
								// Si ya encontramos un individuo con buen fitness lo comparamos con el actual
								if (solutionFound)
									{
										// Si es mejor, lo reemplazamos
										if (_simulationData.calculateFitness(_population.get(i)) >= _simulationData.calculateFitness(_population.get(solutionIndex)))
											{
												solutionFound = true;
												solutionIndex = i;
											}
									}
								else
									{
										solutionFound = true;
										solutionIndex = i;
									}
							}
					}
				// Si encontramos la solucion, lo mostramos por consola!
				if (solutionFound)
					{
						System.out.println("~~~ SOLUTION FOUND!!! ~~~");
						System.out.println("GENERATION: " + _currentGeneration);
						System.out.println("CHROMOSOME: ");
						for (int i = 0; i < _population.get(solutionIndex).genes.size(); i++)
							{
								System.out.println("--->>> " + _population.get(solutionIndex).genes.get(i));
							}
						System.out.println("--->>> DISTANCE: " + _simulationData.calculateDistanceBetweenCities(_population.get(solutionIndex)));
						System.out.println("--->>> FITNESS: " + _simulationData.calculateFitness(_population.get(solutionIndex)));
					}
				return solutionFound;
			}


		private static void showCurrentPopulationOnConsole()
			{
				System.out.println("~~~ GENERATION " + _currentGeneration + " ~~~");
				for (int i = 0; i < _population.size(); i++)
					{
						System.out.println("CITIZEN: " + i);
						for (int j = 0; j < _population.get(i).genes.size(); j++)
							{
								System.out.println("--->>> " + _population.get(i).genes.get(j));
							}
						System.out.println("--->>> DISTANCE: " + _simulationData.calculateDistanceBetweenCities(_population.get(i)));
						System.out.println("--->>> FITNESS: " + _simulationData.calculateFitness(_population.get(i)));
					}
				System.out.println("~~~~~~~~~~~~~~~~~~~~~~");
			}
	}
