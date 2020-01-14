package com.hdstudios.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Menu {

	public String[] options = {"Novo jogo", "Carregar jogo", "Sair"};
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	public boolean up, down, enter, pause;
	
	public void tick() {
		
		if (up) {
			up = false;
			currentOption--;
			if (currentOption < 0) {
				currentOption = maxOption;
			}
		}
		
		if (down) {
			down = false;
			currentOption++;
			if (currentOption > maxOption) {
				currentOption = 0;
			}
		}
		
		if (enter) {
			enter = false;
			if (options[currentOption] == "Novo jogo" || options[currentOption] == "Continuar") {
				Game.gameState = "NORMAL";
				pause = false;
			} else if (options[currentOption] == "Sair") {
				System.exit(1);
			}
		}
		
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 0, 0, 100));
		g2.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.WIDTH*Game.HEIGHT);
		g.setColor(Color.RED);
		g.setFont(new Font("arial", Font.BOLD, 36));
		g.drawString("Game 01", (Game.WIDTH*Game.SCALE/2) - 100, (Game.HEIGHT*Game.SCALE/2) - 100);
		
		//Menu
		g.setFont(new Font("arial", Font.BOLD, 24));
		g.setColor(Color.white);
		
		if (!pause)
			g.drawString("Novo Jogo", (Game.WIDTH*Game.SCALE/2) - 90, (Game.HEIGHT*Game.SCALE/2));
		else
			g.drawString("Continuar", (Game.WIDTH*Game.SCALE/2) - 90, (Game.HEIGHT*Game.SCALE/2));
		
		g.drawString("Carregar Jogo", (Game.WIDTH*Game.SCALE/2) - 110, (Game.HEIGHT*Game.SCALE/2) + 30);
		g.drawString("Sair", (Game.WIDTH*Game.SCALE/2) - 50, (Game.HEIGHT*Game.SCALE/2) + 60);
		
		if (options[currentOption] == "Novo jogo" ) {
			g.drawString(">", (Game.WIDTH*Game.SCALE/2) - 120, (Game.HEIGHT*Game.SCALE/2));
			
		} else if (options[currentOption] == "Carregar jogo" ) {
			g.drawString(">", (Game.WIDTH*Game.SCALE/2) - 130, (Game.HEIGHT*Game.SCALE/2) + 30);
			
		} else if (options[currentOption] == "Sair" ) {
			g.drawString(">", (Game.WIDTH*Game.SCALE/2) - 70, (Game.HEIGHT*Game.SCALE/2) + 60);
			
		}
	}
}
