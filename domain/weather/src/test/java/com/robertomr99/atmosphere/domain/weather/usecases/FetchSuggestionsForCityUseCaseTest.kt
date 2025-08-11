package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.sampleCityCoordinates
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class FetchSuggestionsForCityUseCaseTest {

  @Test
  fun `Invoke calls repository`() {

     val suggestionForCities = flowOf(sampleCityCoordinates("Madrid"))

     val useCase = FetchSuggestionsForCityUseCase( mock {
      on { getSuggestionsForCity("Madrid") } doReturn suggestionForCities
     })

     val result = useCase("Madrid")

     assertEquals(suggestionForCities, result)
  }

 }