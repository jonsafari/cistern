package marmot.ising;

import java.util.ArrayList;
import java.util.Arrays;

public class UnaryFactor extends Factor {

	// variable id
	private int i;
	private int size;
	protected double[] potential;
	
	
	
	public UnaryFactor(int size, int i) {
		this.setSize(size);
		this.setPotential(new double[this.size]);
		
		for (int n = 0; n < this.size; ++n) {
			this.potential[n] = 1.0;
		}
		
		this.setI(i);
		
		this.setNeighbors(new ArrayList<Variable>());
		this.setMessageIds(new ArrayList<Integer>());
		
		this.setMessages(new ArrayList<Message>());
	}

	@Override
	public void passMessage() {
		for (int i = 0; i < this.size; ++i) {
			this.messages.get(0).measure[i] = this.potential[i];
		}
		
		// renormalize (optional)
		this.messages.get(0).renormalize();
		System.out.println(Arrays.toString(this.messages.get(0).measure));
		
	}
	
	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public double[] getPotential() {
		return potential;
	}

	public void setPotential(double[] potential) {
		this.potential = potential;
	}
	
	public void setPotential(int n, double value) {
		this.potential[n] = value;
	}

	

	
	
}