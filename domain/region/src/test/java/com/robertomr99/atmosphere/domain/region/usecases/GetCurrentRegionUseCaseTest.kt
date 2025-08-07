package com.robertomr99.atmosphere.domain.region.usecases

import com.robertomr99.atmosphere.domain.region.data.RegionRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class GetCurrentRegionUseCaseTest{

    @Test
    fun `Invoke calls repository`() : Unit = runBlocking {
        val region = "ES"

        val mockRepository = mock<RegionRepository> {
            onBlocking { findLastRegion() } doReturn region
        }

        val useCase = GetCurrentRegionUseCase(mockRepository)

        val result = useCase()
        assertEquals(region, result)
    }
}