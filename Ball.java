import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.*;
import java.io.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

/**
	Esta classe representa a bola usada no jogo. A classe princial do jogo (Pong)
	instancia um objeto deste tipo quando a execução é iniciada.
*/

public class Ball {
	private double cx, cy, width, height, speed;
	private Color color;
	private double directionX, directionY;
	private Random random = new Random();
	private double buffer = Double.MAX_VALUE;
	private Image img = null;


	/**
		Construtor da classe Ball. Observe que quem invoca o construtor desta classe define a velocidade da bola 
		(em pixels por millisegundo), mas não define a direção deste movimento. A direção do movimento é determinada 
		aleatóriamente pelo construtor.

		@param cx coordenada x da posição inicial da bola (centro do retangulo que a representa).
		@param cy coordenada y da posição inicial da bola (centro do retangulo que a representa).
		@param width largura do retangulo que representa a bola.
		@param height altura do retangulo que representa a bola.
		@param color cor da bola.
		@param speed velocidade da bola (em pixels por millisegundo).
	*/

	public Ball(double cx, double cy, double width, double height, Color color, double speed){
		this.cx = cx;
		this.cy = cy;
		this.width = width;
		this.height = height;
		this.color = color;
		this.speed = speed;
		this.directionX = -1 + 2 * random.nextDouble();
		this.directionY = -1 + 2 * random.nextDouble();
	}


	/**
		Método chamado sempre que a bola precisa ser (re)desenhada.
	*/

	public void draw(){

			GameLib.setColor(this.color);
			GameLib.fillRect(this.getCx(), this.getCy(), this.width, this.height);
	}

	/**
		Método chamado quando o estado (posição) da bola precisa ser atualizado.
		
		@param delta quantidade de millisegundos que se passou entre o ciclo anterior de atualização do jogo e o atual.
	*/

	public void update(long delta){
		this.cy += this.directionY * delta * this.getSpeed();
		this.cx += this.directionX * delta * this.getSpeed();
		buffer += delta;
	}

	/**
		Método chamado quando detecta-se uma colisão da bola com um jogador.
	
		@param playerId uma string cujo conteúdo identifica um dos jogadores.
	*/

	public void onPlayerCollision(String playerId){
		Toolkit.getDefaultToolkit().beep();
		this.directionX = -directionX;

		if(playerId.equals("Player 1")){
			this.directionY += (random.nextDouble() * (0.2));
			try{
			}catch (IllegalArgumentException e){
				this.color = Color.CYAN;
			}

		} else {
			this.directionY -= (random.nextDouble() * (0.2));


		}
		try{ //idk
			if(playerId == "Player 1")
				this.color = new Color(this.color.getRed(), this.color.getGreen() - 10, this.color.getBlue() + 10);
			else
				this.color = new Color(this.color.getRed() - 10, this.color.getGreen() - 10, this.color.getBlue() + 10);
		}
		catch (IllegalArgumentException e){
			this.color = new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
		}
	}

	/**
		Método chamado quando detecta-se uma colisão da bola com uma parede.

		@param wallId uma string cujo conteúdo identifica uma das paredes da quadra.
	*/

	public void onWallCollision(String wallId){
		if(wallId == "Top" || wallId == "Bottom"){
			this.directionY = -(directionY);
			if(directionX > 0)
				this.directionX += (random.nextDouble() * (0.2));
			else
				this.directionX -= (random.nextDouble() * (0.2));
		}
		else{
			try {
				File soundFile;
				if(wallId == "Left"){
					soundFile = new File("oh-my-gah.wav");
				}
				else {
					soundFile = new File("azumanga-daioh-im-sorry.wav");
				}
				AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
				Clip clip = AudioSystem.getClip();
				clip.open(audioIn);
				clip.start();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				e.printStackTrace();
			}
			this.cx = (double) Pong.FIELD_WIDTH/2;
			this.cy = (double) Pong.FIELD_HEIGHT/2;
			this.directionX = -1 + 2 * random.nextDouble();
			this.directionY = -1 + 2 * random.nextDouble();

		}

	}

	/**
		Método que verifica se houve colisão da bola com uma parede.

		@param wall referência para uma instância de Wall contra a qual será verificada a ocorrência de colisão da bola.
		@return um valor booleano que indica a ocorrência (true) ou não (false) de colisão.
	*/
	
	public boolean checkCollision(Wall wall){
		double ballBottom = this.getCy() + (this.height/2);
		double ballTop = this.getCy() - (this.height/2);
		double ballLeft = this.getCx() + (this.height/2);
		double ballRight = this.getCx() - (this.height/2);
		String wallId = wall.getId(); //pega o id da parede para saber onde bateu

		if(wallId == "Bottom" && ballBottom > wall.getCy()){
			return true;
		}
		if(wallId == "Top" && ballTop < wall.getCy()){
			return true;
		}
		if(wallId == "Left" && ballLeft < wall.getCx()){
			return true;
		}
		if(wallId == "Right" && ballRight > wall.getCx()){
			return true;
		}

		return false;
	}

	/**
		Método que verifica se houve colisão da bola com um jogador.

		@param player referência para uma instância de Player contra o qual será verificada a ocorrência de colisão da bola.
		@return um valor booleano que indica a ocorrência (true) ou não (false) de colisão.
	*/	

	public boolean checkCollision(Player player){
		double ballBottom = this.getCy() + (this.height/2);
		double ballTop = this.getCy() - (this.height/2);
		double ballLeft = this.getCx() - (this.width/2);
		double ballRight = this.getCx() + (this.width/2);

		double playerBottom = player.getCy() + (player.getHeight()/2);
		double playerTop = player.getCy() - (player.getHeight()/2);
		double playerLeft = player.getCx() - (player.getWidth()/2);
		double playerRight = player.getCx() + (player.getWidth()/2);

		boolean horizontalCollision = (ballRight >= playerLeft && ballLeft <= playerRight);
		boolean verticalCollision = (ballBottom >= playerTop && ballTop <= playerBottom);

//		if(horizontalCollision && verticalCollision){
//			if (this.directionY > 0 && ballBottom >= playerTop) { // A bola está se movendo para baixo e entrou no player
//				this.cy = playerTop - (this.height / 2);
//			} else if (this.directionY < 0 && ballTop <= playerBottom) { // A bola está se movendo para cima e entrou no player
//				this.cy = playerBottom + (this.height / 2);
//			}
//		}


		if(horizontalCollision && verticalCollision && buffer > 200){
			buffer = 0;
			//this.directionY = -this.directionY;
			return true;
		}
		return false;
		//return (horizontalCollision && verticalCollision && buffer < 200);

	}

	/**
		Método que devolve a coordenada x do centro do retângulo que representa a bola.
		@return o valor double da coordenada x.
	*/
	
	public double getCx(){

		return this.cx;
	}

	/**
		Método que devolve a coordenada y do centro do retângulo que representa a bola.
		@return o valor double da coordenada y.
	*/

	public double getCy(){

		return this.cy;
	}

	/**
		Método que devolve a velocidade da bola.
		@return o valor double da velocidade.

	*/

	public double getSpeed(){

		return this.speed;
	}

}
