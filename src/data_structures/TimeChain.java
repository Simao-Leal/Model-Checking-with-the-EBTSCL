package data_structures;

import java.util.LinkedList;
import java.util.List;

import symbols.TimeSymbol;

public class TimeChain {
	
	private List<List<TimeSymbol>> chain;

	public TimeChain(TimeSymbol t) {
		chain = new LinkedList<>();
		LinkedList<TimeSymbol> inst = new LinkedList<>();
		inst.add(t);
		chain.add(inst);
	}
	
	public void buildLessThan(TimeSymbol t) {
		//adds "< t" to the chain
		LinkedList<TimeSymbol> inst = new LinkedList<>();
		inst.add(t);
		chain.add(inst);
	}
	
	public void buildEqual(TimeSymbol t) {
		//adds "= t" to the chain
		chain.get(chain.size()).add(t);
	}
	
	public TimeChain(String s) { //small parser
		chain = new LinkedList<>();
		String[] instants = s.split("<");
		for(String instant : instants) {
			List<TimeSymbol> inst = new LinkedList<>();
			String[] times = instant.split("=");
			for(String time : times) {
				time = time.trim(); //this was changed from .strip() for compatibility reasons, but it should be ok
				inst.add(new TimeSymbol(time));
			}
			chain.add(inst);
		}
	}
	
	public boolean lessThan(TimeSymbol t1, TimeSymbol t2) {
		for(List<TimeSymbol> instant : chain) {
			if(instant.contains(t1) && (! instant.contains(t2))) {
				return true;
			} else if(instant.contains(t1) && instant.contains(t2)) {
				return false;
			} else if((! instant.contains(t1)) && instant.contains(t2)) {
				return false;
			}
		}
		return false;
	}
	
	public boolean equal(TimeSymbol t1, TimeSymbol t2) {
		for(List<TimeSymbol> instant : chain) {
			if(instant.contains(t1) ^ instant.contains(t2)) {
				return false;
			} else if(instant.contains(t1) && instant.contains(t2)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String res = "";
		for(List<TimeSymbol> instant : chain) {
			for(TimeSymbol time : instant) {
				res += time + " = ";
			}
			res = res.substring(0, res.length() - 3);
			res += " < ";
		}
		return res.substring(0, res.length() - 3);
	}
	
	public static void main(String[] args) {
		TimeChain tsc = new TimeChain("t1 < t2 = t3 < t4");
		System.out.println(tsc);
	}

}
