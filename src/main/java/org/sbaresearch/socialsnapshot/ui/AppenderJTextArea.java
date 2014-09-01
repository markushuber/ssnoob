package org.sbaresearch.socialsnapshot.ui;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This log4j {@link Appender} is used for appending log messages to a given swing {@link JTextArea}.
 *
 * @author Stefan Haider (shaider@sba-research.org) original code: Maurice Wohlkönig
 */
public class AppenderJTextArea extends AppenderSkeleton {

	private JTextArea textArea;
	private Level logLevel;
	private static final int MAX_LOG_ENTRIES = 4096;

	public AppenderJTextArea(JTextArea textArea, final Level logLevel) {
		this.textArea = textArea;
		this.logLevel = logLevel;
	}

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		if (event.getLevel().isGreaterOrEqual(this.logLevel)) {
			if (this.textArea.getLineCount() >= MAX_LOG_ENTRIES) {
				try {
					this.textArea.replaceRange("", this.textArea.getLineStartOffset(0), this.textArea.getLineEndOffset(this.textArea.getLineCount() - MAX_LOG_ENTRIES));
				} catch (BadLocationException e) {
					// nothing
				}
			}
			this.textArea.append(event.getMessage().toString() + "\n");
			this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
		}
	}
}
