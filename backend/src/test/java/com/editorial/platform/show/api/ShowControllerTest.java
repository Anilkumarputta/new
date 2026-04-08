package com.editorial.platform.show.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.editorial.platform.show.api.dto.ShowResponse;
import com.editorial.platform.show.service.ShowService;

@WebMvcTest(ShowController.class)
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShowService showService;

    @Test
    void getAllShowsShouldReturnOk() throws Exception {
        ShowResponse response = new ShowResponse();
        response.setId(1L);
        response.setTitle("Morning Burn");
        response.setCategoryName("Cardio");
        response.setStatus("DRAFT");

        when(showService.getAllShows()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/shows"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Morning Burn"))
            .andExpect(jsonPath("$[0].categoryName").value("Cardio"));
    }
}
