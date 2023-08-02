import java.util.*;

public class BayesianClassifier {
	public static int[][] matrix;
	public static int[][] testDataSet;
	public static int m;
	public static int n;
	public static int[] actualClassification = {1, 0 , 1, 1, 0};    // Assumed
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Enter number of rows of dataset : ");
		m = scanner.nextInt();
		System.out.print("Enter number of columns of dataset : ");
		n = scanner.nextInt();
		
		scanner.close();
		
		matrix = new int[m][n];
		
		for(int i=0 ; i<m ; i++) {
			for(int j=0 ; j<n ; j++) {
				double region  = Math.random();
				if(region < 0.5)
					matrix[i][j] = 0;
				else
					matrix[i][j] = 1;
			}
		}
		
		System.out.println("\nDataset ==>");
		for(int i=0 ; i<m ; i++) {
			for(int j=0 ; j<n ; j++)
				System.out.print(matrix[i][j] + " ");
			System.out.println("");
		}
		
		testDataSet = new int[5][7];
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
		
		System.out.println("\nFor Bayes Classifier ==>");
		bayesClassification();
		
		System.out.println("\nFor Naive Bayes Classifier ==>");
		naiveBayesClassification();
	}
	
	public static void bayesClassification() {
		int[][][] features = new int[n-1][2][2];
		// [n-1] = F1, F2, ...
		// [2] = 0 or 1 in Fi column
		// [2] = 0 or 1 in F(last) column 
		
		for(int i=0 ; i<n-1 ; i++) {
			for(int j=0 ; j<m ; j++) {
				if(matrix[j][i] == 0) {
					if(matrix[j][n-1] == 0)
						features[i][0][0]++;
					else
						features[i][0][1]++;
				}
				else {
					if(matrix[j][n-1] == 0)
						features[i][1][0]++;
					else
						features[i][1][1]++;
				}
			}
		}
		
		// Probability of getting F8 = 1 given that Fi column has 0. Similarly for others.
		double[] prob1_0 = new double[n-1];
		double[] prob0_0 = new double[n-1];		
		double[] prob0_1 = new double[n-1];
		double[] prob1_1 = new double[n-1];
		
		for(int i=0 ; i<n-1 ; i++) {
			prob1_0[i] += (double)features[i][0][1] / (features[i][0][0] + features[i][0][1]);
			prob0_0[i] += (double)features[i][0][0] / (features[i][0][0] + features[i][0][1]);
			prob0_1[i] += (double)features[i][1][0] / (features[i][1][0] + features[i][1][1]);
			prob1_1[i] += (double)features[i][1][1] / (features[i][1][0] + features[i][1][1]);
		}
		
		int[] predictions = new int[testDataSet.length];
		
		for(int i=0 ; i<testDataSet.length ; i++) {
			double prob0 = 0;
			double prob1 = 0;
			
			for(int j=0 ; j<n-1 ; j++) {
				if(j==0 || j==1 || j==2) {
					if(testDataSet[i][j] == 0){
						prob0 += (prob0_0[j] * 0.3);
						prob1 += (prob1_0[j] * 0.3);
					}
					else {
						prob0 += (prob0_1[j]*0.3);
						prob1 += (prob1_1[j]*0.3);
					}
				}
				else if(j==3 || j==4) {
					if(testDataSet[i][j] == 0){
						prob0 += (prob0_0[j] * 0.01);
						prob1 += (prob1_0[j] * 0.01);
					}
					else {
						prob0 += (prob0_1[j]*0.01);
						prob1 += (prob1_1[j]*0.01);
					}
				}
				else {
					if(testDataSet[i][j] == 0){
						prob0 += prob0_0[j];
						prob1 += prob1_0[j];
					}
					else {
						prob0 += prob0_1[j];
						prob1 += prob1_1[j];
					}
				}
			}
			
			if(prob0 > prob1)
				predictions[i] = 0;
			else
				predictions[i] = 1;
		}
		
		System.out.println("Predictions -->");
		for(int i=0 ; i<testDataSet.length ; i++)
			System.out.println(predictions[i]);
		
		confusionMatrix(predictions);
	}
	
	public static void naiveBayesClassification() {
		int[][][] features = new int[n-1][2][2];
		// [n-1] = F1, F2, ...
		// [2] = 0 or 1 in Fi column
		// [2] = 0 or 1 in F(last) column 
		
		for(int i=0 ; i<n-1 ; i++) {
			for(int j=0 ; j<m ; j++) {
				if(matrix[j][i] == 0) {
					if(matrix[j][n-1] == 0)
						features[i][0][0]++;
					else
						features[i][0][1]++;
				}
				else {
					if(matrix[j][n-1] == 0)
						features[i][1][0]++;
					else
						features[i][1][1]++;
				}
			}
		}
		
		// Probability of getting F8 = 1 given that Fi column has 0. Similarly for others.
		double[] prob1_0 = new double[n-1];
		double[] prob0_0 = new double[n-1];		
		double[] prob0_1 = new double[n-1];
		double[] prob1_1 = new double[n-1];
		
		for(int i=0 ; i<n-1 ; i++) {
			prob1_0[i] += (double)features[i][0][1] / (features[i][0][0] + features[i][0][1]);
			prob0_0[i] += (double)features[i][0][0] / (features[i][0][0] + features[i][0][1]);
			prob0_1[i] += (double)features[i][1][0] / (features[i][1][0] + features[i][1][1]);
			prob1_1[i] += (double)features[i][1][1] / (features[i][1][0] + features[i][1][1]);
		}
		
		int[] predictions = new int[testDataSet.length];
		
		for(int i=0 ; i<testDataSet.length ; i++) {
			double prob0 = 0;
			double prob1 = 0;
			
			for(int j=0 ; j<n-1 ; j++) {
				if(testDataSet[i][j] == 0){
					prob0 += prob0_0[j];
					prob1 += prob1_0[j];
				}
				else {
					prob0 += prob0_1[j];
					prob1 += prob1_1[j];
				}
			}
			
			if(prob0 > prob1)
				predictions[i] = 0;
			else
				predictions[i] = 1;
		}
		
		System.out.println("Predictions -->");
		for(int i=0 ; i<testDataSet.length ; i++)
			System.out.println(predictions[i]);
		
		confusionMatrix(predictions);
	}
	
	public static void confusionMatrix(int[] predictions) {
		// Confusion Matrix
		/*
		  -------------------------------------------------------------------
		  |	//////////////// |	Predicted : Yes(1)    |   Predicted : No(0) |
		  ------------------------------------------------------------------
		  |	Actual : Yes(1)	 |		True yes		  |		  False no      |
		  -------------------------------------------------------------------
		  |	Actual : No(0)   |		False yes		  |		  True no       |
		  -------------------------------------------------------------------
		*/
		
		System.out.println("\nConfusion Matrix -->");
		int trueYes = 0;   // Means predicted Yes and actual value is also Yes.
		int trueNo = 0;
		int falseYes = 0;  // Means predicted Yes but actual value is No.
		int falseNo = 0;
		for(int i=0 ; i<5 ; i++) {
			if(actualClassification[i] == 0) {
				if(predictions[i] == 0)
					trueNo++;
				else
					falseYes++;
			}
			else {
				if(predictions[i] == 0)
					falseNo++;
				else
					trueYes++;
			}
		}
		
		System.out.println(trueYes + "\t" + falseNo);
		System.out.println(falseYes + "\t" + trueNo);
		
		double accuracy = (trueYes + trueNo) / 5.0;
		
		System.out.println("\nAccuracy = " + (accuracy*100) + "%");
	}
}






