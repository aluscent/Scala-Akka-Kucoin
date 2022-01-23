package org

case class Symbol(symbol: String, name: String, baseCurrency: String, quoteCurrency: String, feeCurrency: String, market: String,
                  baseMinSize: String, quoteMinSize: String, baseMaxSize: String, quoteMaxSize: String, baseIncrement: String,
                  quoteIncrement: String, priceIncrement: String, priceLimitRate: String, isMarginEnabled: Boolean, enableTrading: Boolean)
