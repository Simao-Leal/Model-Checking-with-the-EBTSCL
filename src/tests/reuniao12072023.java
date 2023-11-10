package tests;

import formulas.Formula;

public class reuniao12072023 {
	public static void main(String[] args) {
		Formula[] forms = new Formula[4];
		forms[0] = Formula.parse("false and true");
		forms[1] = Formula.parse("Simão : hoje . há_sopa and Lourenço : - amanhã . há_sopa");
		forms[2] = Formula.parse("Lourenço <[há_sopa] Simão <[há_sopa] funcionário_da_cantina");
		forms[3] = Formula.parse("ontem < hoje = presente < amanhã = futuro");
		for(Formula phi : forms) {
			 System.out.println(phi);
			 System.out.println(phi.display());
		}
	}
}
