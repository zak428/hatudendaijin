package com.example.hatudendaijin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
// ここは要注意。
// import android.support.v4.app.DialogFragment;だとうまくいかないので、必ず以下にする。
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

@SuppressLint("NewApi")
public class MyDialogFragment extends DialogFragment {
	private DialogListener listener = null;

	/**
	 * ファクトリーメソッド
	 * 
	 * @param type
	 *            ダイアログタイプ 0:OKボタンのみ 1:OK, NGボタン
	 */
	public static MyDialogFragment newInstance(String title, String message,
			int type) {
		MyDialogFragment instance = new MyDialogFragment();

		// ダイアログに渡すパラメータはBundleにまとめる
		Bundle arguments = new Bundle();
		arguments.putString("title", title);
		arguments.putString("message", message);
		arguments.putInt("type", type);

		instance.setArguments(arguments);

		return instance;
	}

	/**
	 * AlertDialog作成
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString("title");
		String message = getArguments().getString("message");
		int type = getArguments().getInt("type");

		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
				.setTitle(title).setMessage(message);
		if (type == 1) {
			alert.setPositiveButton("提出する",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// OKボタンが押された時
							listener.doFinClick();
							dismiss();
						}
					});

			alert.setNegativeButton("キャンセル",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Cancelボタンが押された時
							listener.doNegativeClick();
							dismiss();
						}
					});
		} else if (type == 2) {
			alert.setPositiveButton("リトライ",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// OKボタンが押された時
							listener.doRetryClick();
							dismiss();
						}
					});

			alert.setNegativeButton("あきらめる",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Cancelボタンが押された時
							listener.doGameoverClick();
							dismiss();
						}
					});
		}else if (type == 3) {
			alert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// OKボタンが押された時
							listener.doPositiveClick();
							dismiss();
						}
					});
		}

		return alert.create();
	}

	/**
	 * リスナーを追加
	 */
	public void setDialogListener(DialogListener listener) {
		this.listener = listener;
	}

	/**
	 * リスナー削除
	 */
	public void removeDialogListener() {
		this.listener = null;
	}
}
