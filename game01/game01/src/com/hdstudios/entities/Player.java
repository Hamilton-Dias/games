package com.hdstudios.entities;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import com.hdstudios.main.Game;
import com.hdstudios.world.Camera;
import com.hdstudios.world.World;

public class Player extends Entity{
	
	public boolean right, up, left, down;
	public int right_dir = 0, left_dir=1;
	public int dir = right_dir;
	public double speed = 2;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	public double life = 100, maxLife = 100;
	public int ammo = 0;
	private BufferedImage playerDamage;
	public boolean isDamaged = false;
	private int damageFrames = 0;
	private boolean hasGun = false;
	public boolean shoots = false;
	public boolean mouseShoots = false;
	public int mx, my;
	public boolean jump = false;
	public boolean isJumping = false;
	public int jumpFrames = 50, jumpCur = 0, z = 0;
	public boolean jumpUp = false, jumpDown = false;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		
		for (int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
		}
	}
	
	public void tick() {
		
		if (jump) {
			if (isJumping == false) {
				jump = false;
				isJumping = true;
				jumpUp = true;
			}
		}
		
		if (isJumping == true) {
			if (jumpUp) {
				jumpCur+=2;
			}else if (jumpDown) {
				jumpCur-=2;
				if (jumpCur <= 0) {
					isJumping = false;
					jumpDown = false;
					jumpUp = false;
				}
			}
			z = jumpCur;
			if(jumpCur >= jumpFrames) {
				jumpUp = false;
				jumpDown = true;
			}
		}
		
		moved = false;
		if (right && World.isFree(this.getX() + (int)speed, this.getY())) {
			moved = true;
			x += speed;
			dir = right_dir;
		}
		else if(left && World.isFree(this.getX() - (int)speed, this.getY())) {
			moved = true;
			x -= speed;
			dir = left_dir;
		}
		if (up && World.isFree(this.getX(), this.getY() - (int)speed)) {
			moved = true;
			y -= speed;
		}
		else if (down && World.isFree(this.getX(), this.getY() + (int)speed)) {
			moved = true;
			y += speed;
		}
		
		if (moved) {
			frames++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				if (index > maxIndex) {
					index = 0;
				}
			}
		}
		
		checkLifePack();
		checkAmmo();
		checkGun();
		
		if (isDamaged) {
			this.damageFrames++;
			if (this.damageFrames == 10) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if (shoots) {
			
			shoots = false;
			
			if (hasGun && ammo > 0) {
				
				ammo--;
				int dx = 0;
				int py = 7;
				int px = 0;
				if (dir == right_dir) {
					px = 20;
					dx = 1;
				} else {
					px = -7;
					dx = -1;
				}
				
				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 2, 2, null, dx, 0);
				Game.bullets.add(bullet);
			}
		}
		
		if (mouseShoots) {
			mouseShoots = false;
			
			if (hasGun && ammo > 0) {
				
				ammo--;
				
				int py = 7;
				int px = 0;
				double angle = 0;
				
				if (dir == right_dir) {
					angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x) );
					px = 20;
				} else {
					angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x) );
					px = -7;
				}
				
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				
				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 2, 2, null, dx, dy);
				Game.bullets.add(bullet);
			}
		}
		
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT*16 - Game.HEIGHT);
		
		if (life <= 0) {
			this.life = 0;
			Game.gameState = "GAMEOVER";
		}
		
	}
	
	public void checkGun() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			
			if (e instanceof Weapon) {
				if (Entity.isColliding(this, e)) {
					hasGun = true;
					Game.entities.remove(i);
				}
			}
		}
	}
	
	public void checkAmmo() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			
			if (e instanceof Bullet) {
				if (Entity.isColliding(this, e)) {
					ammo += 10;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	public void checkLifePack() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			
			if (e instanceof LifePack) {
				if (Entity.isColliding(this, e)) {
					life += 8;
					if (life >= 100) 
						life = 100;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if (!isDamaged) {
			if (dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY()  -Camera.y - z, null);
				if (hasGun)
					g.drawImage(Entity.GUN_RIGHT_EN, this.getX() - Camera.x + 8, this.getY() - Camera.y - z, null);
				
			} else if (dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if (hasGun)
					g.drawImage(Entity.GUN_LEFT_EN, this.getX() - Camera.x - 8, this.getY() - Camera.y - z, null);
				
			}
		} else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
			if (hasGun) {
				if (dir == left_dir)
					g.drawImage(Entity.GUN_DAMAGE_LEFT, this.getX() -8 - Camera.x, this.getY()  - Camera.y - z, null);
				else
					g.drawImage(Entity.GUN_DAMAGE_RIGHT, this.getX() + 8 - Camera.x, this.getY() - Camera.y - z, null);
			}
		}
		
		if (isJumping) {
			g.setColor(Color.black);
			g.fillOval(this.getX() - Camera.x + 4, this.getY() - Camera.y +15, 5, 2);
		}
	}
}
