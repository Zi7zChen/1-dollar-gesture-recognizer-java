package $1;

import java.util.Vector;

public class Recognizer {
	//initialize constants
	int NumTemplates=16;
	public static int NumPoints = 64;
	public static double SquareSize = 250.0;
	double Diagonal = Math.sqrt(SquareSize * SquareSize + SquareSize * SquareSize);
	double HalfDiagonal = 0.5 * Diagonal;
	double AngleRange = 45.0;
	double AnglePrecision =2.0;
	public static double Phi = 0.5 * (-1.0 + Math.sqrt(5.0));
	
	public Point centroid=new Point(0,0);
	public Rectangle boundingBox = new Rectangle(0,0,0,0);
	int bounds[] ={0,0,0,0};
	
	Vector Templates = new Vector(NumTemplates);
	
	public Recognizer(){
		//constructor adding templates to the template vector 
		Templates.addElement(addTemp("Triangle",TemplateSet.triangle));
		Templates.addElement(addTemp("X", TemplateSet.x));
		Templates.addElement(addTemp("Rectangle",TemplateSet.rectangle));
		Templates.addElement(addTemp("Circle",TemplateSet.circle));
		Templates.addElement(addTemp("Check", TemplateSet.check));
		Templates.addElement(addTemp("Caret",TemplateSet.caret));
		Templates.addElement(addTemp("Arrow", TemplateSet.arrow));
		Templates.addElement(addTemp("Zig-Zag",TemplateSet.zigzag));
		Templates.addElement(addTemp("Left Square Bracket",TemplateSet.leftSquareBracket));
		Templates.addElement(addTemp("Right Square Bracket",TemplateSet.rightSquareBracket));
		Templates.addElement(addTemp("V", TemplateSet.v));
		Templates.addElement(addTemp("Delete", TemplateSet.delete));
		Templates.addElement(addTemp("Left Curly Brace",TemplateSet.leftCurlyBrace));
		Templates.addElement(addTemp("Right Curly Brace",TemplateSet.rightCurlyBrace));
		Templates.addElement(addTemp("Star", TemplateSet.star));
		Templates.addElement(addTemp("PigTail", TemplateSet.pigTail));
	}
	
	Template addTemp(String name, int[] array){
		//convert the array into points, then store it in a vector 
		Vector v=new Vector(array.length /2);
		for (int i = 0; i < array.length; i += 2) {
			Point p = new Point(array[i], array[i + 1]);
			v.addElement(p);
		}
		//return the template, which has the name of the gesture and the points vector
		return new Template(name, v);
	}
	
	public Result Recognize(Vector points, boolean useProtractor) {
		//perform 1$ to the points
		points = Function.Resample(points, NumPoints);
		points = Function.RotateToZero(points, centroid, boundingBox);
		points = Function.ScaleToSquare(points, SquareSize,null);
		points = Function.TranslateTo(points);
		Vector<Double> vector=Function.Vectorize(points);
		
		//compare the distance score with all the templates
		int temp = 0;
		double b = Double.MAX_VALUE;
		for (int i = 0; i < Templates.size(); i++) {
			double distScore;
			if(useProtractor){
				Template t= (Template) Templates.elementAt(i);
				distScore=Function.OptimalCosineDistance(t.vector,vector);
			}else{
				distScore=Function.BestDistance(points, (Template) Templates.elementAt(i), -AngleRange, AngleRange, AnglePrecision);
			}
			if (distScore < b) {
				b = distScore;
				temp = i;
			}
		}
		//locate the name of the template base on the index
		return new Result(((Template) Templates.elementAt(temp)).name);
	};
}
