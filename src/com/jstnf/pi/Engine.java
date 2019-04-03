package com.jstnf.pi;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.File;

public class Engine extends Canvas implements Runnable
{
	private final int DIGITS = 8;
	private Thread thread;
	private boolean running;
	private int numCollisions;
	private Cube bigCube, smallCube;

	public Engine()
	{
		new Window(1000, 500, "Pi-Engine", this);

		smallCube = new Cube(200, 350, 10, 1, 0);
		bigCube = new Cube(500, 350, 50, Math.pow(100, DIGITS - 1), -0.1);
	}

	public static void main(String[] args)
	{
		new Engine();
	}

	public synchronized void start()
	{
		if (!running)
		{
			thread = new Thread(this);
			thread.start();
			running = true;
		}
	}

	public synchronized void stop()
	{
		try
		{
			thread.join();
			running = false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		long lastTime = System.nanoTime();
		double amountOfTicks = 3000.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while (running)
		{
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1)
			{
				tick();
				delta--;
			}
			if (running)
				render();
			frames++;

			if (System.currentTimeMillis() - timer > 1000)
			{
				timer += 1000;
				System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
		stop();
	}

	private void tick()
	{
		if (smallCube.x <= 40)
		{
			smallCube.reverseDir();
			numCollisions++;
			//			clack();
		}

		bigCube.move();
		smallCube.move();

		if (bigCube.isColliding(smallCube))
		{
			bigCube.doCollide(smallCube);
			numCollisions++;
			//			clack();
			//			getGraphics().drawImage(new ImageIcon("res/pow.png").getImage(), (int) bigCube.x,
			//					(int) (bigCube.y - bigCube.width / 2), 50, 50, null, null);
		}

		System.out.println(numCollisions);
	}

	private void render()
	{
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null)
		{
			this.createBufferStrategy(3);
			return;
		}

		int tempDrawS = (int) smallCube.x;

		if (tempDrawS <= 40)
		{
			tempDrawS = 40;
		}

		int tempDraw = (int) bigCube.x;

		if (tempDraw <= 40 + smallCube.width)
		{
			tempDraw = 40 + smallCube.width;
		}

		Graphics g = bs.getDrawGraphics();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 1000, 500);

		g.setColor(Color.WHITE);
		g.fillRect(35, 0, 5, 500);
		g.fillRect(0, 350, 1000, 5);
		g.drawString(numCollisions + "", tempDrawS, (int) smallCube.y + 25);

		g.setColor(Color.RED);
		g.fillRect(tempDrawS, (int) smallCube.y - smallCube.width, smallCube.width, smallCube.width);

		g.setColor(Color.CYAN);
		g.fillRect(tempDraw, (int) bigCube.y - bigCube.width, bigCube.width, bigCube.width);

		g.dispose();
		bs.show();
	}

	public void clack()
	{
		try
		{
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File("res/clack.wav"));
			DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
			Clip test = (Clip) AudioSystem.getLine(info);

			test.open(ais);
			test.start();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}