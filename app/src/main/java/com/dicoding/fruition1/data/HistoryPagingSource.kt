package com.dicoding.fruition1.data

/*
class HihistoryPagingSource {
}

class HihistoryPagingSource(private val apiService: ApiService) : PagingSource<Int, ListHihistoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListHihistoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(page = position, size = params.loadSize).listHihistory
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListHihistoryItem>): Int? {
        return state.anchorPosition
    }
}*/