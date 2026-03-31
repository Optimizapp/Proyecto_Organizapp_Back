package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.CompanyRequestDto;
import co.javeriana.dw.organizapp.dto.CompanyResponseDto;
import co.javeriana.dw.organizapp.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    @Autowired
    private ObjectMapper objectMapper;

    private CompanyResponseDto companyResponseDto;
    private CompanyRequestDto companyRequestDto;

    @BeforeEach
    void setUp() {
        companyResponseDto = new CompanyResponseDto();
        companyResponseDto.setId(1L);
        companyResponseDto.setName("Test Company");
        companyResponseDto.setNit("123456789");

        companyRequestDto = new CompanyRequestDto();
        companyRequestDto.setName("Test Company");
        companyRequestDto.setNit("123456789");
    }

    @Test
    void getAllCompanies_ShouldReturnList() throws Exception {
        List<CompanyResponseDto> companies = Arrays.asList(companyResponseDto);
        when(companyService.findAll()).thenReturn(companies);

        mockMvc.perform(get("/api/companies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Company")));
    }

    @Test
    void getCompanyById_WhenExists_ShouldReturnCompany() throws Exception {
        when(companyService.findById(1L)).thenReturn(companyResponseDto);

        mockMvc.perform(get("/api/companies/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Company")));
    }

    @Test
    void createCompany_WhenValid_ShouldReturnCreated() throws Exception {
        when(companyService.create(any(CompanyRequestDto.class))).thenReturn(companyResponseDto);

        mockMvc.perform(post("/api/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Company")));
    }

    @Test
    void updateCompany_WhenValid_ShouldReturnOk() throws Exception {
        when(companyService.update(eq(1L), any(CompanyRequestDto.class))).thenReturn(companyResponseDto);

        mockMvc.perform(put("/api/companies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Company")));
    }

    @Test
    void deleteCompany_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/companies/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
