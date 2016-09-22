import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * A kNN classification algorithm implementation.
 * 
 */
public class KNN {
	
	//TODO test with different sets

	/**
	 * In this method, you should implement the kNN algorithm. You can add 
	 * other methods in this class, or create a new class to facilitate your
	 * work. If you create other classes, DO NOT FORGET to include those java
	 * files when preparing your code for hand in.
	 *
	 * Also, Please DO NOT MODIFY the parameters or return values of this method,
	 * or any other provided code.  Again, create your own methods or classes as
	 * you need them.
	 * 
	 * @param trainingData
	 * 		An Item array of training data
	 * @param testData
	 * 		An Item array of test data
	 * @param k
	 * 		The number of neighbors to use for classification
	 * @return
	 * 		The object KNNResult contains classification accuracy, 
	 * 		category assignment, etc.
	 */
	public KNNResult classify(Item[] trainingData, Item[] testData, int k) {
		
		// create return value
		KNNResult result = new KNNResult();
		result.categoryAssignment = new String[testData.length];
		result.nearestNeighbors = new String[testData.length][k];

		// for each test item in testData
		int testIndex = 0;
		for(Item item : testData) {
			
			// find kNN in trainingData
			Item[] distanceSort = itemsByDistance(item, trainingData);
			
			// delete item we are testing
			List<Item> temp = new LinkedList<Item>(Arrays.asList(distanceSort));
			for(int i = 0; i < temp.size(); i++) {
				while(item.name.equals(temp.get(i).name)) {
					temp.remove(i);
				}
			}
			temp.toArray(distanceSort);
			
//			System.out.print(item.name + ": ");
//			for(int i = 0; i < distanceSort.length; i++) {
//				System.out.print(distanceSort[i].name + distance(item, distanceSort[i]) + " ");
//			}
//			System.out.println();
			
			// get predicted category
			List<Category> categoryTotals = new LinkedList<Category>();
			int curIndex = 0;
			for(int i = 0; i < k; i++, curIndex++) {
				Item curItem = distanceSort[curIndex];
				
				// add one to the same category as the current item
				boolean foundCat = false;
				for(Category cat : categoryTotals) {
					if(curItem.category.equals(cat.name)) {
						cat.count++;
						foundCat = true;
					}
				}
				
				// if the category isn't in the list yet, add it to the list
				if(!foundCat) {
					Category toAdd = new Category(curItem.category);
					toAdd.count++;
					categoryTotals.add(toAdd);
				}
			}
			
			// find category with largest number
			Collections.sort(categoryTotals);
			Collections.reverse(categoryTotals);
			int mostVotes = categoryTotals.get(0).count;
			int numLargestCategories = 1;
			for(curIndex = 1; curIndex < categoryTotals.size(); curIndex++) {
				if(categoryTotals.get(curIndex).count >= mostVotes) {
					numLargestCategories++;
				} else {
					break;
				}
			}

			String votedCat = categoryTotals.get(0).name;
			
			// search if list contains "fruit"
			for(int i = 0; i < numLargestCategories; i++) {
				if(categoryTotals.get(i).name.equals("fruit")) {
					votedCat = "fruit";
				}
			}
			
			// search if list contains "machine"
			for(int i = 0; i < numLargestCategories; i++) {
				if(categoryTotals.get(i).name.equals("machine")) {
					votedCat = "machine";
				}
			}
			
			// search if list contains "nation"
			for(int i = 0; i < numLargestCategories; i++) {
				if(categoryTotals.get(i).name.equals("nation")) {
					votedCat = "nation";
				}
			}
			
			// save category in KNNResult.categoryAssignment
			result.categoryAssignment[testIndex] = votedCat;
			
			// save kNN in KNNResult.nearestNeighbors
			for(int i = 0; i < k; i++) {
				result.nearestNeighbors[testIndex][i] = distanceSort[i].name;
			}
			
			testIndex++;
		}
		
		// calculate accuracy
		int numCorrect = 0;
		for(int i = 0; i < testData.length; i++) {
			if(testData[i].category.equals(result.categoryAssignment[i])) {
				numCorrect++;
			}
		}
		result.accuracy = ((double)numCorrect / (double)testData.length);

		return result;
	}
	
	/**
	 * Calculates the Euclidean distance between two Items baled on their
	 * features.
	 * @param a The first Item
	 * @param b The second Item
	 * @return The Euclidean distance between the two Items
	 */
	public static double distance(Item a, Item b) {
		double sum = 0;
		
		// add the squared distance between each feature
		for(int i = 0; i < 3; i++) {
			double aVal = a.features[i];
			double bVal = b.features[i];
			double distance = aVal - bVal;
			sum += distance * distance;
		}
		
		// return Euclidean distance
		return Math.sqrt(sum);
	}
	
	/**
	 * Returns an array sorted by distance to the specified Item
	 * @param a the Item to compare distance with
	 * @param list the list of Items to compare
	 * @return an array of the sorted Items
	 */
	public static Item[] itemsByDistance(Item a, Item[] list) {
		Item[] items = Arrays.copyOf(list, list.length);
		Arrays.sort(items, new ItemComparable(a));
		return items;
	}
	
	/**
	 * Comparable interface to compare Items by Euclidean distance.
	 *
	 */
	static class ItemComparable implements Comparator<Item>{
		 
		private Item main; // the item to compare distance to
		
		/**
		 * The constructor
		 * @param main the Item to compare all other items to
		 */
		public ItemComparable(Item main) {
			this.main = main;
		}
		
	    /* (non-Javadoc)
	     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	     */
	    @Override
	    public int compare(Item a, Item b) {
	    	return Double.compare(distance(main, a), distance(main, b));
	    }
	}
	
	/**
	 * A object to represent Item categories comparable by a count.
	 *
	 */
	static class Category implements Comparable<Category>{
		
		private String name; // name of the category
		private int count; // current count
		
		/**
		 * Constructor for a category
		 * @param name
		 */
		public Category(String name) {
			this.name = name;
			count = 0;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Category o) {
			return (this.count - o.count);
		}
		
	}
	
}