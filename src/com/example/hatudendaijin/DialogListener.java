package com.example.hatudendaijin;

import java.util.EventListener;

public interface DialogListener extends EventListener{

    /**
     * OKボタンが押されたイベントを通知
     */
    public void doPositiveClick();
    
    /**
     * Cancelボタンが押されたイベントを通知
     */
    public void doNegativeClick();
    
    /**
     * 提出ボタンが押されたイベントを通知
     */
    public void doFinClick();
    /**
     * リトライボタンが押されたイベントを通知
     */
    public void doRetryClick();
    /**
     * あきらめるボタンが押されたイベントを通知
     */
    public void doGameoverClick();
}
