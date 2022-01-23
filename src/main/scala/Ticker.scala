package org

case class Ticker(time: Long, sequence: String, price: String, size: String,
                  bestBid: String, bestBuySize: String, bestAsk: String, bestAskSize: String)