package com.robertomr99.atmosphere.domain.region.usecases

import com.robertomr99.atmosphere.domain.region.data.RegionDataSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class GetCurrentRegionUseCaseTest{

    @Test
    fun `Invoke calls repository`() : Unit = runBlocking {
        val region = "ES"

        val mockDataSource = mock<RegionDataSource> {
            onBlocking { findLastRegion() } doReturn region
        }

        val useCase = GetCurrentRegionUseCase(mockDataSource)

        val result = useCase()
        assertEquals(region, result)
    }
}