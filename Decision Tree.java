import java.util.*;

public class DecisionTree {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter number of rows and columns respectively : ");
		int m = scanner.nextInt();
		int n = scanner.nextInt();
		scanner.close();
		
		int[][] dataset = new int[m][n];
		for(int i=0 ; i<m ; i++) {
			for(int j=0 ; j<n ; j++) {
				dataset[i][j] = (int)(Math.random() * 2);
			}
		}
		
//		//Custom Dataset
//		int[] r1 = {0, 1, 1, 1, 1, 1, 0, 0};
//		int[] r2 = {0, 1, 1, 1, 0, 1, 1, 0};
//		int[] r3 = {1, 0, 1, 0, 1, 1, 1, 0};
//		int[] r4 = {1, 0, 1, 1, 1, 0, 1, 0};
//		int[] r5 = {0, 0, 1, 0, 0, 0, 0, 0};
//		int[] r6 = {0, 0, 1, 0, 1, 0, 1, 0};
//		int[] r7 = {1, 1, 0, 1, 0, 1, 1, 0};
//		int[] r8 = {1, 1, 1, 0, 0, 0, 0, 0};
//		int[] r9 = {1, 1, 1, 1, 1, 1, 0, 0};
//		int[] r10 = {1, 1, 0, 1, 0, 0, 1, 0};
//		
//		dataset[0] = r1;
//		dataset[1] = r2;
//		dataset[2] = r3;
//		dataset[3] = r4;
//		dataset[4] = r5;
//		dataset[5] = r6;
//		dataset[6] = r7;
//		dataset[7] = r8;
//		dataset[8] = r9;
//		dataset[9] = r10;
		
		System.out.println("\nTraining DataSet :");
		for(int i=0 ; i<m ; i++) {
			for(int j=0 ; j<n ; j++)
				System.out.print(dataset[i][j] + " ");
			System.out.println();
		}
		System.out.println("\n");
		
		int[][] positives = new int[n-1][2]; 
		int[][] negatives = new int[n-1][2];
		// Since we have oly 0(negatives) and 1(positives), so these are two values.
		// Number of No(0) and Yes(1) respectively for a particular value is stored 
		// in array of size 2.
		
		for(int i=0 ; i<n-1 ; i++) {
			for(int j=0 ; j<m ; j++) {
				if(dataset[j][n-1] == 1) {
					if(dataset[j][i] == 1)
						positives[i][1]++;
					else
						negatives[i][1]++;
				}
				else{
					if(dataset[j][i] == 1)
						positives[i][0]++;
					else
						negatives[i][0]++;
				}
			}
		}
		
		double maxInformationGain = Integer.MIN_VALUE;
		int maxInformationGainColumn = -1;
		for(int i=0 ; i<n-1 ; i++) {
			double ig = informationGain(positives, negatives, i);
			if(ig > maxInformationGain) {
				maxInformationGain = ig;
				maxInformationGainColumn = i;
			}
		}
		
		TreeNode root = new TreeNode(maxInformationGainColumn);
		
		boolean[] featureUsed = new boolean[n-1];
		boolean[] canUseRowValue = new boolean[m];
		Arrays.fill(canUseRowValue, true);
		
		Queue<TreeNode> queue = new LinkedList<>();
		Queue<boolean[]> status = new LinkedList<>();
		Queue<boolean[]> featureUsedTill = new LinkedList<>();
		
		queue.add(root);
		featureUsed[root.column] = true;
		status.add(canUseRowValue);
		featureUsedTill.add(featureUsed);
		
		while(!queue.isEmpty()) {
			TreeNode node = queue.poll();
			boolean[] currentStatus = status.poll();
			boolean[] currentFeatureUsed = featureUsedTill.poll();
			
			for(int val=0 ; val<2 ; val++) {
				boolean[] temp = currentStatus.clone();
				boolean[] featureUsedTemp = currentFeatureUsed.clone();
				
				for(int i=0 ; i<m ; i++) {
					if(dataset[i][node.column] != val)
						temp[i] = false;
				}
				
				positives = new int[n-1][2];
				negatives = new int[n-1][2];
				
				for(int i=0 ; i<n-1 ; i++) {
					for(int j=0 ; j<m ; j++) {
						if(temp[j] == true) {
							if(dataset[j][n-1] == 1) {
								if(dataset[j][i] == 1)
									positives[i][1]++;
								else
									negatives[i][1]++;
							}
							else{
								if(dataset[j][i] == 1)
									positives[i][0]++;
								else
									negatives[i][0]++;
							}
						}
					}
				}
				
				maxInformationGain = Integer.MIN_VALUE;
				maxInformationGainColumn = -1;
				
				for(int i=0 ; i<n-1 ; i++) {
					if(featureUsedTemp[i] == false) {
						double ig = informationGain(positives, negatives, i);
						if(ig > maxInformationGain) {
							maxInformationGain = ig;
							maxInformationGainColumn = i;
						}
					}
				}
				
				int column = node.column;
				Map<Integer, Integer> zero = new HashMap<>();
				// If we choose zero value(left) of feature then what results we can get
				Map<Integer, Integer> one = new HashMap<>();
				// If we choose one value(right) of feature then what results we can get
				
				boolean zeroOne = false;
				boolean zeroZero = false;
				boolean oneZero = false;
				boolean oneOne = false;
				
				int zeroPlusOne = 0;
				
				for(int i=0 ; i<m ; i++) {
					if(temp[i] == true) {
						zeroPlusOne++;
						if(dataset[i][n-1] == 1) {
							if(dataset[i][column] == 1) {
								one.put(1, one.getOrDefault(1, 0) + 1);
								oneOne = true;
							}
							else {
								zero.put(1, zero.getOrDefault(1, 0) + 1);
								zeroOne = true;
							}
						}
						else{
							if(dataset[i][column] == 1) {
								one.put(0, one.getOrDefault(0, 0) + 1);
								oneZero = true;
							}
							else {
								zero.put(0, zero.getOrDefault(0, 0) + 1);
								zeroZero = true;
							}
						}
					}
				}
				
				if(zeroZero && zero.get(0) == zeroPlusOne) {
					node.leftLeaf = 0;
					continue;
				}
				else if(zeroOne && zero.get(1) == zeroPlusOne) {
					node.leftLeaf = 1;
					continue;
				}
				
				if(oneZero && one.get(0) == zeroPlusOne) {
					node.rightLeaf = 0;
					continue;
				}
				else if(oneOne && one.get(1) == zeroPlusOne) {
					node.rightLeaf = 1;
					continue;
				}
				
				if(maxInformationGainColumn == -1) {
					if(val == 0)
						node.leftLeaf = (zeroZero && zeroOne && zero.get(0) > zero.get(1))? 0 : 1;
					else
						node.rightLeaf = (oneZero && oneOne && one.get(0) > one.get(1))? 0 : 1;
					
					continue;
				}
				
				if(val == 0) {
					node.left = new TreeNode(maxInformationGainColumn);
					queue.add(node.left);
				}
				else {
					node.right = new TreeNode(maxInformationGainColumn);
					queue.add(node.right);
				}
				
				status.add(temp);
				featureUsedTemp[maxInformationGainColumn] = true;
				featureUsedTill.add(featureUsedTemp);
			}
		}
		
		System.out.println("Decision Tree :");
		printDecisionTree(root);
		
		int[][] testDataSet = new int[5][7];
		int[] t1 = {1, 0, 1, 0, 0, 1, 1};
		int[] t2 = {1, 0, 0, 1, 0, 0, 1};
		int[] t3 = {1, 0, 0, 0, 1, 1, 1};
		int[] t4 = {1, 0, 0, 0, 1, 0, 1};
		int[] t5 = {1, 0, 0, 0, 0, 0, 0};
		testDataSet[0] = t1;
		testDataSet[1] = t2;
		testDataSet[2] = t3;
		testDataSet[3] = t4;
		testDataSet[4] = t5;
		
		int[] predictions = new int[5];
		
		predict(testDataSet, root, predictions);
		
		System.out.println("\nPredictions for F8 are :");
		for(int i=0 ; i<5 ; i++)
			System.out.println(predictions[i]);
	}
	
	public static double informationGain(int[][] positives, int[][] negatives, int column) {
		double p = positives[column][1] + negatives[column][1];
		double n = positives[column][0] + negatives[column][0];
		double summation = (positives[column][0] + positives[column][1]) / (p+n) * entropy(positives[column][1], positives[column][0]);
		summation += (negatives[column][0] + negatives[column][1]) / (p+n) * entropy(negatives[column][1], negatives[column][0]);
		
		double ig = entropy(p, n) - summation;
		
		return ig;
	}
	
	public static double entropy(double positives, double negatives) {
		if(positives==0 && negatives==0)
			return 0;
		double p = positives / (positives + negatives);
		double n = negatives / (positives + negatives);
		double e;
		if(p==0 && n==0)
			e = 0;
		else if(p == 0)
			e = - (n*(Math.log(n)/Math.log(2)));
		else if(n == 0)
			e = - (p*(Math.log(p)/Math.log(2)));
		else
			e = -( (p*(Math.log(p)/Math.log(2))) + (n*(Math.log(n)/Math.log(2))) );
		return e;
	}
	
	public static void predict(int[][] testDataSet, TreeNode root, int[] predictions) {
		TreeNode temp = root;
		for(int i=0 ; i<testDataSet.length ; i++) {
			root = temp;
			while(true) {
				int f = root.column;
				if(testDataSet[i][f] == 0) {
					if(root.left == null) {
						predictions[i] = root.leftLeaf;
						break;
					}
					root = root.left;
				}
				else {
					if(root.right == null) {
						predictions[i] = root.rightLeaf;
						break;
					}
					root = root.right;
				}
			}
		}
	}
	
	public static int heightOfTree(TreeNode root) {
        if (root == null)
            return 0;
            
        return 1 + Math.max(heightOfTree(root.left), heightOfTree(root.right));
    }
	
	public static void printSpace(double n, TreeNode removed) {
        for ( ; n>0 ; n--)
            System.out.print("  ");
        
        if (removed == null)
            System.out.print(" ");
        else
            System.out.print(removed.column);
    }
	
	public static void printDecisionTree(TreeNode root) {
        LinkedList<TreeNode> treeLevel = new LinkedList<TreeNode>();
        treeLevel.add(root);
        
        LinkedList<TreeNode> temp = new LinkedList<TreeNode>();
        
        int counter = 0;
        int height = heightOfTree(root) - 1;
        
        double numberOfElements = Math.pow(2, (height + 1)) - 1;
        
        while (counter <= height) {
            TreeNode removed = treeLevel.removeFirst();
            if (temp.isEmpty())
                printSpace(numberOfElements / Math.pow(2, counter + 1), removed);
            else
                printSpace(numberOfElements / Math.pow(2, counter), removed);
            
            if (removed == null) {
                temp.add(null);
                temp.add(null);
            }
            else {
                temp.add(removed.left);
                temp.add(removed.right);
            }
 
            if (treeLevel.isEmpty()) {
                System.out.println("");
                System.out.println("");
                treeLevel = temp;
                temp = new LinkedList<>();
                counter++;
            }
        }
    }
}


class TreeNode{
	TreeNode left = null;
	TreeNode right = null;
	int leftLeaf = -1;
	int rightLeaf = -1; 
	int column = -1;
	
	public TreeNode(int clm) {
		this.column = clm;
	}
}






