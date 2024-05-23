package com.example.storyapp.viewmodel

import MainDispatcherRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.data.remote.response.ListStoryDetail
import com.example.storyapp.data.repository.MainRepository
import com.example.storyapp.view.adapter.ListStoryAdapter
import com.example.storyapp.viewmodel.DataDummy.generateDummyNewStories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    private lateinit var mainViewModel: MainViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: MainRepository

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(repository)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `when get story should not null and return data`() = runTest {
        val dummyStories = generateDummyNewStories()
        val data: PagingData<ListStoryDetail> = PagedTestDataSources.snapshot(dummyStories)
        val expectedStory = MutableLiveData<PagingData<ListStoryDetail>>()
        val token = "token dummy"

        expectedStory.value = data
        Mockito.`when`(repository.getPagingStories(token)).thenReturn(expectedStory)

        val actualStory: PagingData<ListStoryDetail> = mainViewModel.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.StoryDetailDiffCallback(),
            updateCallback = updateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories, differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `when get story empty should return no data`() = runTest {
        val data: PagingData<ListStoryDetail> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryDetail>>()
        val token = "token dummy"

        expectedStory.value = data
        Mockito.`when`(repository.getPagingStories(token)).thenReturn(expectedStory)
        val actualStory: PagingData<ListStoryDetail> = mainViewModel.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.StoryDetailDiffCallback(),
            updateCallback = updateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        assertEquals(0, differ.snapshot().size)
    }

    private val updateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    class PagedTestDataSources private constructor() :
        PagingSource<Int, ListStoryDetail>() {
        companion object {
            fun snapshot(items: List<ListStoryDetail>): PagingData<ListStoryDetail> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, ListStoryDetail>): Int? {
            return null
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryDetail> {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }
    }
}
