package jp.ne.oskd.techverify.selenide;

import lombok.Data;

/**
 * Alertの表示結果格納クラス
 */
@Data
public class DialogResult {
    public enum AlertHandling {
        Accept, Dismis;
    }

    /**
     * 表示有無
     */
    private boolean disp;

    /**
     * 表示メッセージ
     */
    private String message;
}
