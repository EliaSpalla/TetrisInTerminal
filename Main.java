public class Main{
	final static int AltezzaGriglia=20;
	final static int LunghezzaGriglia=10;
	static boolean griglia[][]=new boolean[AltezzaGriglia][LunghezzaGriglia];

	public static void main(String[] args){
		stampaGriglia();
	}

	public static void stampaGriglia(){
		for(int i=0;i<AltezzaGriglia;i++){
			System.out.print("<| ");
			for(int j=0;j<LunghezzaGriglia;j++){
				System.out.print((griglia[i][j]) ? "[ ] " : "    ");
			}
			System.out.println(" |>");
		}
	}
}
