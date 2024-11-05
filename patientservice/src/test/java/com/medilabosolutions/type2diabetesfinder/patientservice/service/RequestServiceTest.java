package com.medilabosolutions.type2diabetesfinder.patientservice.service;

import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * unit test class for the RequestService.
 * @author olivier morel
 */
public class RequestServiceTest {
	
	private RequestService requestService;
	private MockHttpServletRequest requestMock;
	private WebRequest request;
	
	@BeforeEach
	public void setUpPerTest() {
		requestService = new RequestServiceImpl();
	}
	
	@AfterEach
	public void undefPerTest() {
		requestService = null;
	}
	
	@Test
	@Tag("RequestServiceTest")
	@DisplayName("requestToString test chain a WebRequest uri+parameters into a String")
	public void requestToStringTest() {
		//GIVEN
		requestMock = new MockHttpServletRequest();
		requestMock.setServerName("http://localhost:8080");
		requestMock.setRequestURI("/phoneAlert");
		String[] params = {"1", "2"};
		requestMock.setParameter("firestation", params);
		request = new ServletWebRequest(requestMock);
		//WHEN
		String parameters = requestService.requestToString(request);
		//THEN
		assertThat(parameters).isEqualTo("uri=/phoneAlert?firestation=1,2");
	}
}