package com.ninemax;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ConcurrentTest {
	private static int thread_num = 60;// 200;
	private static int client_num = 100;// 460;

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		ExecutorService exec = Executors.newCachedThreadPool();
		// thread_num���߳̿���ͬʱ����
		final Semaphore semp = new Semaphore(thread_num);
		// ģ��2000���ͻ��˷���
		for (int index = 0; index < client_num; index++) {
			final int NO = index;
			exec.execute(new TaskThread(semp, NO));
		}
		
		long timeSpend = (System.currentTimeMillis() - start) / 1000;
		System.out.println("����1: " + timeSpend + "��");
		// �˳��̳߳�
		exec.shutdown();
		timeSpend = System.currentTimeMillis() - start;
		System.out.println("����2: " + timeSpend + "��");
		// long end = (System.currentTimeMillis()-start)/1000;//��ǰʱ���뵱��0��ĺ�����

	}

	static class TaskThread implements Runnable {
		Semaphore semp;
		int NO;

		public TaskThread(Semaphore semp, int NO) {
			this.semp = semp;
			this.NO = NO;
		}

		public void run() {
			try {
				// ��ȡ���
				semp.acquire();
				System.out.println("Thread:" + NO);
				TestRedisLua test=new TestRedisLua();
				test.exce();
				// System.out.println(result);
				 Thread.sleep((long) (Math.random()) * 1000);
				// �ͷ�
				//System.out.println("�ڣ�" + NO + " ��");
				// System.out.println(result);
				semp.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

	}

}
