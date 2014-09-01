package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class Currency implements Serializable {
	
	private static final long serialVersionUID = 4453815259877800706L;
	@Expose
	@SerializedName("user_currency")
	String userCurrency;
	@Expose
	@SerializedName("currency_exchange")
	double currencyExchange;
	@Expose
	@SerializedName("currency_exchange_inverse")
	double currencyExchangeInverse;
	@Expose
	@SerializedName("currency_offset")
	double currencyOffset;
}
