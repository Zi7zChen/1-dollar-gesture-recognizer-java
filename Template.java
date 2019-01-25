package $1;

import java.util.*;
//Template class 
public class Template {
	public String name;
	public Vector points;
	public Vector vector;// for protractor
	public Template(String name, Vector points){
		this.name=name;
		this.points = Function.Resample(points, Recognizer.NumPoints);
		this.points = Function.RotateToZero(this.points,null,null);
		this.points = Function.ScaleToSquare(this.points, Recognizer.SquareSize,null);
		this.points = Function.TranslateTo(this.points);
		this.vector = Function.Vectorize(this.points);
	}
}
