package Base;
import java.awt.Point;
import java.util.ArrayList;

public class SimulationData
{
	private final Point[] _cities = new Point[] {	new Point(60, 200),
													new Point(180, 200),
													new Point(80, 180),
													new Point(140, 180),
													new Point(20, 160),
													new Point(100, 160),
													new Point(200, 160),
													new Point(140, 140),
													new Point(40, 120),
													new Point(100, 120),
													new Point(180, 100),
													new Point(60, 80),
													new Point(120, 80),
													new Point(180, 60),
													new Point(20, 40),
													new Point(100, 40),
													new Point(200, 40),
													new Point(20, 20),
													new Point(60, 20),
													new Point(160, 20)};
	
	
	
	
	public int getAmountOfCities() 
	{ 
		return _cities.length;
	}
	
	
	public float bestApproximateFitness()
	{
		//Numero aproximado de la distancia ideal
		float bestApproximateDistance = 820f;
		return 1 / bestApproximateDistance;
	}
	
	/**
	 * Ordena los elementos de la lista dejando primeros a los de mayor fitness.
	 * Aplica BubbleSort
	 * @param itemsToOrder Lista de elementos a ordenar
	 * @return La misma lista, ordenada segun Fitness
	 */
	public ArrayList<Chromosome> sortItemsByFitness(ArrayList<Chromosome> itemsToSort)
	{	
		//Calcula el valor fitness de los cromosomas
		for (Chromosome chromosome : itemsToSort) 
		{
			chromosome.fitness = calculateFitness(chromosome);
		}
		
		//Los ordena de mayor a menor
		int n = itemsToSort.size();
		Chromosome temp = null;
		for(int i=0; i < n; i++)
		{
			for(int j=1; j < (n-i); j++)
			{                   
				if(itemsToSort.get(j-1).fitness < itemsToSort.get(j).fitness)
				{
					temp = itemsToSort.get(j-1);
					itemsToSort.set(j-1, itemsToSort.get(j));
					itemsToSort.set(j, temp);
				}                    
			}
		}
     
		return itemsToSort;
	}
	
	/**
	 * Selecciona elementos de forma randomica pero teniendo en cuenta su Fitness como probabilidad.
	 * @param itemsToChoose Lista de elementos desde la cual se obtendran los items.
	 * @param amountOfItemsToSelect Cantidad de elementos seleccionados al azar
	 * @return Lista de elementos seleccionados aleatoriamente, basandose en probabilidades
	 */
	public ArrayList<Chromosome> getItemsByRoulletteWheelSelection(ArrayList<Chromosome> itemsToChoose, int amountOfItemsToSelect)
	{
		ArrayList<Chromosome> result = new ArrayList<Chromosome>();
		
		for (int i = 0; i < amountOfItemsToSelect; i++)
		{
			float totalSum = 0;
		    float runningSum = 0;
		    
		    for (Chromosome c : itemsToChoose)
		    {
		    	totalSum += c.fitness;
		    }

		    float rnd = (float) (Math.random() * totalSum);

		    for (Chromosome c : itemsToChoose)
		    {
		    	if ( rnd >= runningSum && rnd <= runningSum + c.fitness)
		        {
		    		result.add(c);
		    		itemsToChoose.remove(c);
		    		break;
		        }
		        runningSum += c.fitness;
		    }
		}
		
	    return result;
	}
	
	
	public float calculateFitness(Chromosome chromosome)
	{
		//Obtenemos la distancia basada en los genes del cromosoma.
		float possibleSolutionDistance = calculateDistanceBetweenCities(chromosome);
		
		//Calculamos un valor de fitness. Cuanto mas alto, mejor!
		return 1 / possibleSolutionDistance;
	}
	
	/**
	   * Este metodo realiza una sumatoria 
	   * entre las distancias de la ciudades,
	   * basado en el orden de las mismas en el cromosoma.
	   */
	public float calculateDistanceBetweenCities(Chromosome chromosome)
	{
		
		float distance = 0;
		for (int i = 0; i < chromosome.genes.size()-1; i++) 
		{
			distance += _cities[chromosome.genes.get(i)].distance(_cities[chromosome.genes.get(i+1)]);
		}
		
		return distance;
	}
}
