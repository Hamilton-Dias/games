package com.hdstudios.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.hdstudios.main.Game;
import com.hdstudios.main.Sound;
import com.hdstudios.world.Camera;
import com.hdstudios.world.World;

public class Enemy extends Entity{

	private double speed = 0.4;
	private int maskx = 8, masky = 8, maskw = 10, maskh = 10;
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 1;
	private BufferedImage[] sprites;
	private int life = 3;
	private boolean isDamaged = false;
	private int damageFrames = 10, curFrames = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(112+16, 16, 16, 16);
	}

	public void tick() {
		
		if (isCollidingWithPlayer() == false) {
		
			if ((int)x < Game.player.getX() && World.isFree((int)(this.getX() + speed), this.getY())
					&& !isColliding((int)(this.getX() + speed), this.getY())) {
				x += speed;
			}
			else if ((int)x > Game.player.getX() && World.isFree((int)(this.getX() - speed), this.getY())
					&& !isColliding((int)(this.getX() - speed), this.getY())) {
				x -= speed;
			}
			
			if ((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(this.getY() + speed))
					&& !isColliding(this.getX(), (int)(this.getY() + speed))) {
				y += speed;
			}
			else if ((int)y > Game.player.getY()&& World.isFree(this.getX(), (int)(this.getY() - speed))
					&& !isColliding(this.getX(), (int)(this.getY() - speed))) {
				y -= speed;
			}
		} else {
			//estamos colidindo com o jogador
			if (Game.rand.nextInt(100) < 20) {
				Sound.hurtEffect.play();
				Game.player.life -= Game.rand.nextInt(4);
				Game.player.isDamaged = true;
				if (Game.player.life == 0) {
					System.exit(1);
				}
				
			}
		}
		
		frames++;
		if (frames == maxFrames) {
			frames = 0;
			index++;
			if (index > maxIndex) {
				index = 0;
			}
		}
		
		collidingBullet();
		
		if (life <= 0) {
			Game.entities.remove(this);
			Game.enemies.remove(this);
			return;
		}
		
		if (isDamaged) {
			this.curFrames++;
			
			if(this.curFrames == this.damageFrames) {
				this.curFrames = 0;
				this.isDamaged = false;
			}
		}
	}
	
	public void collidingBullet() {
		for (int i = 0; i < Game.bullets.size(); i++) {
			
			Entity e = Game.bullets.get(i);
			if (e instanceof BulletShoot) {
				
				if (Entity.isColliding(this, e)) {
					
					isDamaged = true;
					life--;
					Game.bullets.remove(e);
					return;
				}
			}
		}
	}
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + maskx, maskw, maskh);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColliding(int xNext, int yNext) {
		Rectangle enemyCurrent = new Rectangle(xNext + maskx, yNext + maskx, maskw, maskh);
		
		for (int i = 0; i < Game.enemies.size(); i++) {
			
			Enemy e = Game.enemies.get(i);			
			if (e == this) 
				continue;
			
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + maskx, maskw, maskh);
			if (enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
			
		}
		
		return false;
	}
	
	public void render(Graphics g) {
		if (!isDamaged)
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		else
			g.drawImage(Entity.ENEMY_FEEDBACK_EN, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
