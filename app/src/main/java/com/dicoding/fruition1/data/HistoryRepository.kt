package com.dicoding.fruition1.data

class HistoryRepository {
    /*
        private val orderFruits = mutableListOf<OrderFruit>()

        init {
            if (orderFruits.isEmpty()) {
                FakeFruitDataSource.dummyFruits.forEach {
                    orderFruits.add(OrderFruit(it, 0))
                }
            }
        }

        fun getAllFruits(): Flow<List<OrderFruit>> {
            return flowOf(orderFruits)
        }

        fun getOrderFruitById(rewardId: Long): OrderFruit {
            return orderFruits.first {
                it.reward.id == rewardId
            }
        }

        fun updateOrderFruit(rewardId: Long, newCountValue: Int): Flow<Boolean> {
            val index = orderFruits.indexOfFirst { it.reward.id == rewardId }
            val result = if (index >= 0) {
                val orderFruit = orderFruits[index]
                orderFruits[index] =
                    orderFruit.copy(reward = orderFruit.reward, count = newCountValue)
                true
            } else {
                false
            }
            return flowOf(result)
        }

        fun getAddedOrderFruits(): Flow<List<OrderFruit>> {
            return getAllFruits()
                .map { orderFruits ->
                    orderFruits.filter { orderFruit ->
                        orderFruit.count != 0
                    }
                }
        }*/

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null

        fun getInstance(): HistoryRepository =
            instance ?: synchronized(this) {
                HistoryRepository().apply {
                    instance = this
                }
            }
    }
}