package com.example.hatudendaijin;

import android.content.Context;
import android.media.MediaPlayer;

public class BgmPlayer {
	private MediaPlayer mediaPlayer;

	public BgmPlayer(Context context, int i) {
		// BGMファイルを読み込む
		if (i == 1) {
			this.mediaPlayer = MediaPlayer.create(context, R.raw.the_coastline);
			// ループ再生
			this.mediaPlayer.setLooping(true);
		} else if (i == 2) {
			this.mediaPlayer = MediaPlayer.create(context, R.raw.applause);
		} else if (i == 3) {
			this.mediaPlayer = MediaPlayer.create(context,R.raw.pearls_are_hopping_in_a_cup);
			// ループ再生
			this.mediaPlayer.setLooping(true);
		} else if (i == 4) {
			this.mediaPlayer = MediaPlayer.create(context,R.raw.look_back_on);
			// ループ再生
			this.mediaPlayer.setLooping(true);
		}else if (i == 5) {
			this.mediaPlayer = MediaPlayer.create(context,R.raw.omotya);
		}else if (i == 6) {
			this.mediaPlayer = MediaPlayer.create(context,R.raw.ed);
		}else if (i == 7) {
			this.mediaPlayer = MediaPlayer.create(context,R.raw.letstudy);
		}
		// 音量設定
		this.mediaPlayer.setVolume(1.0f, 1.0f);
	}

	/**
	 * BGMを再生する
	 */
	public void start() {
		if (!mediaPlayer.isPlaying()) {
			mediaPlayer.seekTo(0);
			mediaPlayer.start();
		}
	}

	/**
	 * BGMを停止する
	 */
	public void stop() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.prepareAsync();
		}
	}
}