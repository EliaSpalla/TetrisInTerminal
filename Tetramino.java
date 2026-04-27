public abstract class Tetramino{

	public void ruota(boolean[][] tetramino){
		int lunghezzaTetramino=tetramino.length;

		for(int i=0;i<lunghezzaTetramino;i++){
			for(int j=0;j<lunghezzaTetramino;j++){
				boolean temp=tetramino[i][j];
				tetramino[i][j]=tetramino[j][i];
				tetramino[j][i]=temp;
			}
		}

		for(int i=0;i<lunghezzaTetramino;i++){
			for(int j=0;j<lunghezzaTetramino/2;j++){
				boolean temp=tetramino[i][j];
				tetramino[i][j]=tetramino[j][i];
				tetramino[j][i]=temp;
			}
		}
	}
}
