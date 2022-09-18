package com.example.apigateway.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("itemExchange");
    }

    @Bean
    public Queue queue() {
        return new Queue("productQueue");
    }
    @Bean
    public Queue queue2() {
        return new Queue("componentQueue");
    }
    @Bean
    public Binding binding(DirectExchange directExchange,
                           Queue queue) {
        return BindingBuilder.bind(queue)
                .to(directExchange)
                .with("createProduct");
    }
    @Bean
    public Binding binding2(DirectExchange directExchange,
                           Queue queue2) {
        return BindingBuilder.bind(queue2)
                .to(directExchange)
                .with("getInformation");
    }

    @Bean
    public AsyncRabbitTemplate asyncRabbitTemplate(
            RabbitTemplate rabbitTemplate){
        return new AsyncRabbitTemplate(rabbitTemplate);
    }
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
