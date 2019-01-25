package $1;

import java.util.*;
//Algorithms taken from the website, somewhat optimized to reduce the length of the code 

public class Function {
	public static Vector Resample(Vector p, int n){
		double I = length(p)/(n-1);
		double D = 0.0;
		Vector newPoint = new Vector(n);
		newPoint.addElement(p.elementAt(0));
		
		for (int i=1; i<p.size();i++){
			Point p1=(Point) p.elementAt(i-1);
			Point p2=(Point) p.elementAt(i);
			
			double d = distance(p1,p2);
			if((D+d)>=I){
				double qx =p1.getX() +((I-D)/d) * (p2.getX() - p1.getX());
				double qy =p1.getY() +((I-D)/d) * (p2.getY() - p1.getY());
				Point q = new Point(qx,qy);
				newPoint.addElement(q);
				p.insertElementAt(q, i);
				D=0.0;
			}
			else {
				D+=d;
			}
		}
		if(newPoint.size()==n-1){
			newPoint.addElement(p.elementAt(p.size()-1));
		}
		return newPoint;
	}
	
	public static Vector RotateToZero(Vector points, Point centroid,Rectangle boundingBox) {
		Point c= Centroid(points);
		Point first = (Point)points.elementAt(0);
		double theta = Math.atan2(c.getY() - first.getY(),c.getX() - first.getX());
		if (centroid != null)
			centroid.copy(c);

		if (boundingBox != null)
			BoundingBox(points, boundingBox);
		
		return RotateByRadians(points, -theta);
	}
	
	public static Vector RotateByRadians(Vector p, double radians){
		Vector newPoints = new Vector(p.size());
		Point c = Centroid(p);
		double cos=Math.cos(radians);
		double sin=Math.sin(radians);
		double cx = c.getX();
		double cy = c.getY();
		for(int i=0; i<p.size();i++){
			Point ptr = (Point) p.elementAt(i);
			double dx = ptr.getX() - cx;
			double dy = ptr.getY() - cy;
			newPoints.addElement(new Point(dx*cos-dy*sin+cx,dx*sin+dy*cos+cy));
		}
		return newPoints;
	}
	public static Point Centroid(Vector p){
		double xsum = 0.0;
		double ysum = 0.0;
		
		Iterator<Point> itr= p.iterator();
		while(itr.hasNext()){
			Point pt=itr.next();
			xsum+=pt.getX();
			ysum+=pt.getY();
		}
		return new Point(xsum / p.size(), ysum / p.size());
	}
	
	public static Vector ScaleToSquare(Vector point, double size, Rectangle boundingBox){
		Rectangle B = BoundingBox(point);
		Vector newPoints= new Vector(point.size());
		for(int i=0; i<point.size();i++){
			Point p=(Point)point.elementAt(i);
			double qx = p.getX() * (size / B.width);
			double qy = p.getY() * (size / B.height);
			newPoints.addElement(new Point(qx,qy));
		}
		return newPoints;
	}
	public static Rectangle BoundingBox(Vector points) {
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;

		Iterator<Point> itr= points.iterator();
		while(itr.hasNext()){
			Point pt=itr.next();
			if (pt.getX() < minX)
				minX = pt.getX();
			if (pt.getX() > maxX)
				maxX = pt.getX();
			if (pt.getY() < minY)
				minY = pt.getY();
			if (pt.getY() > maxY)
				maxY = pt.getY();
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
	public static void BoundingBox(Vector points, Rectangle dst){
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		Iterator<Point> itr= points.iterator();
		while(itr.hasNext()){
			Point pt=itr.next();
			if (pt.getX() < minX)
				minX = pt.getX();
			if (pt.getX() > maxX)
				maxX = pt.getX();
			if (pt.getY() < minY)
				minY = pt.getY();
			if (pt.getY() > maxY)
				maxY = pt.getY();
		}
		dst.x = minX;
		dst.y = minY;
		dst.width = maxX - minX;
		dst.height = maxY - minY;
	}
	
	public static Vector TranslateTo(Vector point){
		Point c=Centroid(point);
		Vector newPoints= new Vector(point.size());
		for(int i=0; i< point.size();i++){
			Point p = (Point)point.elementAt(i);
			double qx = p.getX()-c.getX();
			double qy = p.getY()-c.getY();
			newPoints.addElement(new Point(qx,qy));
		}
		return newPoints;
	}
	
	public static double length(Vector p){
		double length =0;
		for(int i=1; i<p.size();i++){
			length+=distance((Point)p.elementAt(i-1),(Point)p.elementAt(i));
		}
		return length;
	}
	public static double distance(Point p1, Point p2){
		double dx=p2.getX() -p1.getX();
		double dy=p2.getY() -p1.getY();
		return Math.sqrt((dx*dx)+(dy*dy));
	}
	public static double pathDistance(Vector p1, Vector p2){
		double distance=0;
		for(int i=0; i<p1.size();i++){
			distance+= distance((Point)p1.elementAt(i), (Point)p2.elementAt(i));
		}
		return (distance/p1.size());
		}
	public static double DistanceAtAngle(Vector p, Template T, double theta){
		Vector newPoints=RotateByRadians(p,theta);
		return pathDistance(newPoints, T.points);
	}
	public static double BestDistance(Vector p, Template t, double a, double b, double threshold){
		double phi=Recognizer.Phi;
		double x1=(phi*a)+((1.0-phi)*b);
		double r1=DistanceAtAngle(p,t,x1);
		double x2=((1.0-phi)*a)+(phi*b);
		double r2=DistanceAtAngle(p,t,x2);
		while(Math.abs(b-a)>threshold){
			if(r1<r2){
				b=x2;
				x2=x1;
				r2=r1;
				x1=(phi*a)+((1.0-phi)*b);
				r1=DistanceAtAngle(p,t,x1);
			}
			else{
				a=x1;
				x1=x2;
				r1=r2;
				x2=(1.0-phi)*a+(phi*b);
				r2=DistanceAtAngle(p,t,x2);
			}
		}
		return Math.min(r1, r2);
	}
	/*
	 * 
	 * 
	 * 
	 * 
	 */
	public static Vector<Double> Vectorize(Vector p){
		double sum =0;
		Vector<Double> vector=new Vector();
		Vector<Double>finalPoints=new Vector();
		for(int i=0; i<p.size();i++){
			Point pp=(Point) p.get(i);
			vector.add(pp.getX());
			vector.add(pp.getY());
			sum+= pp.getX() * pp.getX() + pp.getY()*pp.getY();
		}
		double Magnitude = Math.sqrt(sum);
		for(int i=0; i<vector.size(); i++){
			double newVal=vector.elementAt(i)/Magnitude;
			finalPoints.add(newVal);
		}
		return finalPoints;
	}
	
	public static double OptimalCosineDistance(Vector<Double >v1, Vector<Double> v2){
		double a=0;
		double b=0;
		for(int i=0; i<v1.size();i+=2){
			a+=v1.elementAt(i)*v2.elementAt(i) + v1.elementAt(i+1)*v2.elementAt(i+1);
			b+=v1.elementAt(i)*v2.elementAt(i+1) - v1.elementAt(i+1) *v2.elementAt(i);
		}
		double angle = Math.atan(b/a);
		return Math.acos(a*Math.cos(angle) + b*Math.sin(angle));
	}
};
