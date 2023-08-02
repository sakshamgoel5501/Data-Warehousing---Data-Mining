import java.util.*;

public class Clustering {
	public static Point[] p;
	public static int n;
	public static int overallCentroidX = 0;
	public static int overallCentroidY = 0;
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Number of points in DataSet : ");
		n = scanner.nextInt();
		
		p = new Point[n];
		
		System.out.println("\nData Points ==> ");
		
		for(int i=0 ; i<n ; i++) {
			int x = 0;
			int y = 0;
			while(x == 0)
				x = (int)(Math.random() * 100);
			while(y == 0)
				y = (int)(Math.random() * 100);
			p[i] = new Point(x, y);
			
			System.out.println(String.format("(%d , %d)", x, y));
		}
		
		// Custom Data points
//		p = new Point[10];
//		p[0] = new Point(78, 70);
//		System.out.println(String.format("(%d , %d)", 78, 70));
//		p[1] = new Point(16, 67);
//		System.out.println(String.format("(%d , %d)", 16, 67));
//		p[2] = new Point(60, 44);
//		System.out.println(String.format("(%d , %d)", 60, 44));
//		p[3] = new Point(8, 83);
//		System.out.println(String.format("(%d , %d)", 8, 83));
//		p[4] = new Point(49, 71);
//		System.out.println(String.format("(%d , %d)", 49, 71));
//		p[5] = new Point(21, 33);
//		System.out.println(String.format("(%d , %d)", 21, 33));
//		p[6] = new Point(49, 49);
//		System.out.println(String.format("(%d , %d)", 49, 49));
//		p[7] = new Point(26, 29);
//		System.out.println(String.format("(%d , %d)", 26, 29));
//		p[8] = new Point(26, 70);
//		System.out.println(String.format("(%d , %d)", 26, 70));
//		p[9] = new Point(61, 52);
//		System.out.println(String.format("(%d , %d)", 61, 52));
		
//		p = new Point[10];
//		p[0] = new Point(0, 0);
//		p[1] = new Point(1, 0);
//		p[2] = new Point(0, 1);
//		p[3] = new Point(10, 10);
//		p[4] = new Point(11, 10);
//		p[5] = new Point(10, 11);
//		p[6] = new Point(100, 100);
//		p[7] = new Point(101, 101);
//		p[8] = new Point(100, 100);
//		p[9] = new Point(101, 100);
		
		for(int i=0 ; i<n ; i++) {
			int x = p[i].x;
			int y = p[i].y;
			overallCentroidX += x;
			overallCentroidY += y;
		}
		
		overallCentroidX = overallCentroidX / n;
		overallCentroidY = overallCentroidY / n;
		
		System.out.println("\nFor K-Means ==>");
		
		System.out.print("\nEnter k value : ");
		int k = scanner.nextInt();
		
		kMeans(k);
		
		System.out.println("\nFor AHC ==>");
		
		AHC();
		
		System.out.println("\nFor DBSCAN ==>");
				
		System.out.print("MinPoints : ");
		int minPoints = scanner.nextInt();
		
		System.out.print("Epsilon : ");
		int epsilon = scanner.nextInt();
		
		scanner.close();
		
		DBSCAN(minPoints, epsilon);
	}
	
	public static void kMeans(int k) {
		if(n < k) {
			System.out.println("\nData size not enough to make given number of clusters !!");
			return;
		}
		
		Point[] kPoint = new Point[k];
		
		for(int i=0, j=0 ; i<k ; i++, j++) {
			int kx = p[j].x;
			int ky = p[j].y;
			kPoint[i] = new Point(kx, ky);
		}
		
		int[] inWhichCluster = new int[n];
		Arrays.fill(inWhichCluster, -1);
		
		Set<Integer>[] cluster = new HashSet[k];
		for(int i=0 ; i<k ; i++)
			cluster[i] = new HashSet<Integer>();
		
		for(int i=0 ; i<n ; i++) {
			double dist = Double.MAX_VALUE;
			for(int j=0 ; j<k ; j++) {
				double euclid = euclidean(p[i].x, p[i].y, kPoint[j].x, kPoint[j].y);
				if(euclid < dist) {
					dist = euclid; 
					int idx = inWhichCluster[i];
					if(idx != -1)
						cluster[idx].remove(i);
					cluster[j].add(i);
					inWhichCluster[i] = j;
				}
			}
		}
		
		while(true) {
			for(int j=0 ; j<k ; j++) {
				int size = cluster[j].size();
				if(size == 0)
					continue;
				double sumX = 0;
				double sumY = 0;
				for(int pnt : cluster[j]) {
					Point temp = p[pnt];
					sumX += temp.x;
					sumY += temp.y;
				}
				double gx = sumX / size;
				double gy = sumY / size;
				kPoint[j].x = (int)gx;
				kPoint[j].y = (int)gy;
			}
			
			Set<Integer>[] clusterNew = new HashSet[k];
			for(int i=0 ; i<k ; i++)
				clusterNew[i] = new HashSet<Integer>();
			
			int[] inWhichClusterNew = new int[n];
			Arrays.fill(inWhichClusterNew, -1);
			
			for(int i=0 ; i<n ; i++) {
				double dist = Double.MAX_VALUE;
				for(int j=0 ; j<k ; j++) {
					double euclid = euclidean(p[i].x, p[i].y, kPoint[j].x, kPoint[j].y); 
					if(euclid < dist) {
						dist = euclid;
						int idx = inWhichClusterNew[i];
						if(idx != -1)
							clusterNew[idx].remove(i);
						clusterNew[j].add(i);
						inWhichClusterNew[i] = j;
					}
				}
			}
			
			if(change(inWhichCluster, inWhichClusterNew) == true) {
				cluster = clusterNew;
				inWhichCluster = inWhichClusterNew;
			}
			else
				break;
		}
		
		int clusterNum = 1;
		
		for(int i=0 ; i<k ; i++) {
			System.out.print("Cluster " + clusterNum + " --> ");
			clusterNum++;
			System.out.print(String.format("Centroid (%d , %d) : ", kPoint[i].x, kPoint[i].y));
			int len = cluster[i].size();
			int count = 1;
			for(int pnt : cluster[i]) {
				Point temp = p[pnt];
				int x = temp.x;
				int y = temp.y;
				if(count == len)
					System.out.print(String.format("(%d , %d)", x, y));
				else
					System.out.print(String.format("(%d , %d) , ", x, y));
				count++;
			}
			System.out.println();
		}
		
		double totalIntraClusterSSE = 0;
		double interClusterSSE = 0;
		
		for(int i=0 ; i<k ; i++)
			totalIntraClusterSSE += intraClusterSSE(cluster[i], p, kPoint[i].x, kPoint[i].y);
		
		interClusterSSE = interClusterSSE(overallCentroidX, overallCentroidY, kPoint);
		
		System.out.println("\nTotal Intra-Cluster SSE = " + totalIntraClusterSSE);
		System.out.println("Inter-Cluster SSE = " + interClusterSSE);
		
		double sc = totalIntraClusterSSE / interClusterSSE;
		System.out.println(String.format("\nScatter coefficient : %.4f", sc));
	}
	
	public static void AHC() {
		int[][] proximityMatrix = new int[n][n];
		
		for(int i=0 ; i<n ; i++) {
			for(int j=0 ; j<n ; j++) {
				proximityMatrix[i][j] = manhattanDist(p[i].x, p[i].y, p[j].x, p[j].y);
			}
		}
		
		Cluster[] cluster = new Cluster[n];
		
		List<Cluster> clustersFormed = new ArrayList<Cluster>();
		
		for(int i=0 ; i<n ; i++) {
			cluster[i] = new Cluster(p[i].x, p[i].y);
			cluster[i].points.add(i);
			clustersFormed.add(cluster[i]);
		}
	
		int size = n;
		
		int time = 1;
		int maxGap = -1;
		int seeBelowTime = 1;
		int previousPeek = 0;
		
		while(n > 1) {
			int minDist = Integer.MAX_VALUE;
			
			int c = -1;
			int r = -1;
			for(int i=0 ; i<n ; i++) {
				for(int j=i+1 ; j<n ; j++) {
					if(manhattanDist(cluster[i].x, cluster[i].y, cluster[j].x, cluster[j].y) < minDist) {
						minDist = manhattanDist(cluster[i].x, cluster[i].y, cluster[j].x, cluster[j].y);
						c = i;
						r = j;
					}
				}
			}
			
			Cluster[] updatedCluster = new Cluster[n-1];
			
			for(int i=0, idx=0 ; i<n ; i++) {
				if(i==c || i==r)
					continue;
				updatedCluster[idx] = cluster[i];
				idx++;
			}
			
			updatedCluster[n-2] = new Cluster(0, 0);
			for(int data : cluster[c].points)
				updatedCluster[n-2].points.add(data);
			for(int data : cluster[r].points)
				updatedCluster[n-2].points.add(data);
			
			int meanX = (cluster[c].x + cluster[r].x) / 2;
			int meanY = (cluster[c].y + cluster[r].y) / 2;
			
			updatedCluster[n-2].x = meanX;
			updatedCluster[n-2].y = meanY;
			updatedCluster[n-2].horizontalPeek = manhattanDist(cluster[c].x, cluster[c].y, cluster[r].x, cluster[r].y);
			updatedCluster[n-2].time = time;
			time++;
			
			clustersFormed.add(updatedCluster[n-2]);
			
			if((updatedCluster[n-2].horizontalPeek - previousPeek) > maxGap) {
				maxGap = updatedCluster[n-2].horizontalPeek - previousPeek;
				seeBelowTime = updatedCluster[n-2].time;
			}
			
			previousPeek = updatedCluster[n-2].horizontalPeek;

			n--;
			
			cluster = new Cluster[n];
			cluster = updatedCluster;
			
			proximityMatrix = new int[n][n];
			
			for(int i=0 ; i<n ; i++) {
				for(int j=0 ; j<n ; j++) {
					proximityMatrix[i][j] = manhattanDist(cluster[i].x, cluster[i].y, cluster[j].x, cluster[j].y);
				}
			}
		}
		
		n = size;
		
		Set<Integer> pointsConsidered = new HashSet<Integer>();
		
		int clusterNum = 1;
		
		List<Integer> clustersConsidered = new ArrayList<Integer>();
		
		for(int i=clustersFormed.size()-1 ; i>=0 ; i--) {
			Cluster c = clustersFormed.get(i);
			
			if(pointsConsidered.size() == n)
				break;
			
			if(c.time < seeBelowTime) {
				int len = c.points.size();
				int count = 1;
				boolean considered = true;

				for(int pnt : c.points) {
					if(pointsConsidered.contains(pnt) == false) {
						System.out.print("Cluster " + clusterNum + " --> ");
						clusterNum++;
						clustersConsidered.add(i);
						System.out.print(String.format("Centroid (%d , %d) : ", c.x, c.y));
					}
					break;
				}
				
				for(int pnt : c.points) {
					if(pointsConsidered.contains(pnt)) {
						considered = false;
						break;
					}
					pointsConsidered.add(pnt);
					Point temp = p[pnt];
					int x = temp.x;
					int y = temp.y;
					if(count == len)
						System.out.print(String.format("(%d , %d)", x, y));
					else
						System.out.print(String.format("(%d , %d) , ", x, y));
					count++;
				}
				
				if(considered)
					System.out.println();
			}
		}
		
		double totalIntraClusterSSE = 0;
		double interClusterSSE = 0;
		
		int numClusters = clusterNum - 1;
		
		Point[] clusterCentroids = new Point[numClusters];
		
		for(int i=0 ; i<numClusters ; i++) {
			int temp = clustersConsidered.get(i);
			Cluster c = clustersFormed.get(temp);
			int sumX = 0;
			int sumY = 0;
			
			for(int idx : c.points) {
				int x = p[idx].x;
				int y = p[idx].y;
				sumX += x;
				sumY += y;
			}
			
			sumX = sumX / c.points.size();
			sumY = sumY / c.points.size();
			
			clusterCentroids[i] = new Point(sumX, sumY);
		}
		
		for(int i=0 ; i<numClusters ; i++) {
			int temp = clustersConsidered.get(i);
			Cluster c = clustersFormed.get(temp);
			totalIntraClusterSSE += intraClusterSSE(c.points, p, clusterCentroids[i].x, clusterCentroids[i].y);
		}
		
		interClusterSSE = interClusterSSE(overallCentroidX, overallCentroidY, clusterCentroids);
		
		System.out.println("\nTotal Intra-Cluster SSE = " + totalIntraClusterSSE);
		System.out.println("Inter-Cluster SSE = " + interClusterSSE);
		
		double sc = totalIntraClusterSSE / interClusterSSE;
		System.out.println(String.format("\nScatter coefficient : %.4f", sc));
	}
	
	public static void DBSCAN(int minPoints, int epsilon) {
		List<Integer>[] near = new ArrayList[n];
		
		Set<Integer> core = new HashSet<Integer>();
		Set<Integer> border = new HashSet<Integer>();
		Set<Integer> noise = new HashSet<Integer>();
		
		for(int i=0 ; i<n ; i++) {
			Point p1 = p[i];
			near[i] = new ArrayList<Integer>();
			
			for(int j=0 ; j<n ; j++) {
				Point p2 = p[j];
				if(euclidean(p1.x, p1.y, p2.x, p2.y) <= epsilon) {
					near[i].add(j);
				}
			}
			
			if(near[i].size() >= minPoints)
				core.add(i);
		}
		
		for(int i=0 ; i<n ; i++) {
			if(core.contains(i) == false) {
				int flag = 0;
				
				for(int point : near[i]) {
					if(core.contains(point)) {
						border.add(i);
						flag++;
						break;
					}
				}
				
				if(flag == 0)
					noise.add(i);
			}
		}
		
		System.out.println("\nCore Points -->");
		if(core.size() >= 1) {
			for(int idx : core)
				System.out.println("(" + p[idx].x + ", " + p[idx].y + ")");
		}
		else
			System.out.println("No Core points for this dataset.");
		
		System.out.println("\nBorder Points -->");
		if(border.size() >= 1) {
			for(int idx : border)
				System.out.println("(" + p[idx].x + ", " + p[idx].y + ")");
		}
		else
			System.out.println("No Border points for this dataset.");
		
		System.out.println("\nNoise Points -->");
		if(noise.size() >= 1) {
			for(int idx : noise)
				System.out.println("(" + p[idx].x + ", " + p[idx].y + ")");
		}
		else
			System.out.println("No Noise points for this dataset.");
		
		List<List<Integer>> clusters = new ArrayList<List<Integer>>();
		boolean[] considered = new boolean[n];
		
		while(true) {
			boolean run = false;
			int take = 0;
			
			for(int i=0 ; i<n ; i++) {
				if(considered[i] == false) {
					run = true;
					take = i;
					break;
				}
			}
			
			if(run == false)
				break;
			
			List<Integer> cluster = new ArrayList<Integer>();
			
			Queue<Integer> queue = new LinkedList<Integer>();
			
			for(int point : near[take]) {
				if(considered[point] == false) {
					queue.add(point);
					considered[point] = true;
				}
			}
			
			while(!queue.isEmpty()) {
				int temp = queue.poll();
				
				for(int point : near[temp]) {
					if(considered[point] == false) {
						queue.add(point);
						considered[point] = true;
					}
				}
				
				cluster.add(temp);
			}
			
			clusters.add(cluster);
		}
		
		int numClusters = clusters.size();
		
		Point[] clusterCentroids = new Point[numClusters];
		
		for(int i=0 ; i<numClusters ; i++) {
			List<Integer> cluster = clusters.get(i);
			int sumX = 0;
			int sumY = 0;
			
			for(int idx : cluster) {
				int x = p[idx].x;
				int y = p[idx].y;
				sumX += x;
				sumY += y;
			}
			
			sumX = sumX / cluster.size();
			sumY = sumY / cluster.size();
			
			clusterCentroids[i] = new Point(sumX, sumY);
		}
		
		for(int i=0 ; i<numClusters ; i++) {
			System.out.print("\nCluster " + (i+1) + " --> ");
			System.out.print("Centroid (" + clusterCentroids[i].x + ", " + clusterCentroids[i].y + ") : ");
			
			List<Integer> cluster = clusters.get(i);
			
			for(int j=0 ; j<cluster.size()-1 ; j++) {
				int point = cluster.get(j);
				System.out.print("(" + p[point].x + ", " + p[point].y + ") , ");
			}
			
			int point = cluster.get(cluster.size()-1);
			System.out.print("(" + p[point].x + ", " + p[point].y + ")");
		}
		
		double totalIntraClusterSSE = 0;
		double interClusterSSE = 0;
		
		for(int i=0 ; i<numClusters ; i++) {
			Set<Integer> tempCluster = new HashSet<Integer>();
			for(int idx : clusters.get(i))
				tempCluster.add(idx);
			totalIntraClusterSSE += intraClusterSSE(tempCluster, p, clusterCentroids[i].x, clusterCentroids[i].y);
		}
		
		interClusterSSE = interClusterSSE(overallCentroidX, overallCentroidY, clusterCentroids);
		
		System.out.println("\n\nTotal Intra-Cluster SSE = " + totalIntraClusterSSE);
		System.out.println("Inter-Cluster SSE = " + interClusterSSE);
		
		double sc = totalIntraClusterSSE / interClusterSSE;
		System.out.println(String.format("\nScatter coefficient : %.4f", sc));
	}
	
	public static double euclidean(double x1, double y1, double x2, double y2) {
		double x = Math.pow(x2-x1, 2);
		double y = Math.pow(y2-y1, 2);
		return Math.sqrt(x + y);
	}
	
	public static int manhattanDist(int x1, int y1, int x2, int y2) {
		int dist = Math.abs(x2-x1) + Math.abs(y2-y1);
		return dist;
	}
	
	public static boolean change(int[] inWhichCluster1, int[] inWhichCluster2) {
		int len = inWhichCluster1.length;
		
		for(int i=0 ; i<len ; i++) {
			if(inWhichCluster1[i] != inWhichCluster2[i])
				return true;
		}
		
		return false;
	}
	
	public static double intraClusterSSE(Set<Integer> cluster, Point[] p, int kx, int ky) {
		double sumSqDist = 0;
		
		for(int pnt : cluster) {
			int x = p[pnt].x;
			int y = p[pnt].y;
			double X = Math.pow(x-kx, 2);
			double Y = Math.pow(y-ky, 2);
			double sqDist = X + Y;
			sumSqDist += sqDist;
		}
		
		return sumSqDist;
	}
	
	public static double interClusterSSE(int gx, int gy, Point[] kPoint) {
		double sumSqDist = 0;
		
		for(int i=0 ; i<kPoint.length ; i++) {
			int x = kPoint[i].x;
			int y = kPoint[i].y;
			double X = Math.pow(x-gx, 2);
			double Y = Math.pow(y-gy, 2);
			double sqDist = X + Y;
			sumSqDist += sqDist;
		}
		
		return sumSqDist;
	}
}

class Point {
	int x;
	int y;
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

class Cluster {
	Set<Integer> points;
	int x;
	int y;
	int horizontalPeek;
	int time;
	public Cluster(int x, int y) {
		this.x = x;
		this.y = y;
		this.horizontalPeek = 0;
		this.time = 0;
		this.points = new HashSet<Integer>();
	}
}











