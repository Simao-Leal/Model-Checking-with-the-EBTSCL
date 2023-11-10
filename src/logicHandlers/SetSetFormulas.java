package logicHandlers;

import java.util.HashSet;

import formulas.Formula;

@SuppressWarnings("serial")
public class SetSetFormulas extends HashSet<SetFormulas> {
	
	public SetSetFormulas() {
		super();
	}
	
	@Override
	public String toString() {
		String s = "{";
		for(SetFormulas set : this) {
			s += set + ",\n ";
		}
		if(s.length() > 1) s = s.substring(0, s.length()-3);
		s += "}";
		return s;
	}
	
	public String toLaTeX() {
		String s = "\\{";
		for(SetFormulas set : this) {
			s += set.toLaTeX();
			s += ", ";
		}
		s = s.substring(0, s.length() - 2);
		s += "\\}";
		return s;
	}

}
