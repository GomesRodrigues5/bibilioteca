package util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Filtro que permite apenas dígitos numéricos (0-9)
 */
public class DigitOnlyFilter extends DocumentFilter {

    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string != null && string.matches("[0-9]*")) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text != null && text.matches("[0-9]*")) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
