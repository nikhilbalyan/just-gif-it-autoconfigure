package com.justgifit;

import java.io.File;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.filter.RequestContextFilter;

import com.justgifit.services.ConverterService;
import com.justgifit.services.GifEncoderService;
import com.justgifit.services.VideoDecoderService;
import com.madgag.gif.fmsware.AnimatedGifEncoder;

@Configuration
@ConditionalOnClass({FFmpegFrameGrabber.class, AnimatedGifEncoder.class})
public class JustGifItAutoConfiguration {
	
	@Value("${multipart.location}/gif/")
	private String gifLocation;
	
//	@PostConstruct
	@Bean
	@ConditionalOnProperty(prefix = "com.justgifit", name= "create-result-dir")
	public Boolean createResultDir() {
		File gifFolder = new File(gifLocation);
		if(!gifFolder.exists()) {
			gifFolder.mkdir();
		}
		return true;
	}

	@Bean
	@ConditionalOnMissingBean(VideoDecoderService.class)
	public VideoDecoderService videoDecoderService() {
		return new VideoDecoderService();
	}
	
	@Bean
	@ConditionalOnMissingBean(ConverterService.class)
	public GifEncoderService gifEncoderService() {
		return new GifEncoderService();
	}
	
	@Bean
	@ConditionalOnMissingBean(VideoDecoderService.class)
	public ConverterService converterService() {
		return new ConverterService();
	}
	
	@Configuration
	@ConditionalOnWebApplication
	public static class WebConfiguration {
		
		@Value("${multipart.location}/gif/")
		private String gifLocation;
		
	    // we are disabling the filter here 
	    @Bean
	    @ConditionalOnProperty(prefix = "com.justgifit", name="optimize") // if com.justgifitoptimize parameter is set to true these filters will be deregistered
	    public FilterRegistrationBean deRegisterHiddenHttpMethodFilter
	    (HiddenHttpMethodFilter filter) {
	    	FilterRegistrationBean bean = new FilterRegistrationBean(filter);
	    	bean.setEnabled(false);
	    	return bean;
	    }
	    // here also we have disabled a filter
	    @Bean
	    @ConditionalOnProperty(prefix = "com.justgifit", name="optimize") // if com.justgifitoptimize parameter is set to true these filters will be deregistered
	    public FilterRegistrationBean
	    deRegisterHttpPutFormContentFilter (HttpPutFormContentFilter filter) {
	    	FilterRegistrationBean bean = new FilterRegistrationBean(filter);
	    	bean.setEnabled(false);
	    	return bean;
	    }
	    
	    // disabling requestcontextfilter
	    @Bean
	    @ConditionalOnProperty(prefix = "com.justgifit", name="optimize") // if com.justgifitoptimize parameter is set to true these filters will be deregistered
	    public FilterRegistrationBean
	    deRegisterRequestContextFilter (RequestContextFilter filter) {
	    	FilterRegistrationBean bean = new FilterRegistrationBean(filter);
	    	bean.setEnabled(false);
	    	return bean;
	    }

	}
}
