package com.salesmanager.shop.store.controller.shoppingCart;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.shop.application.ShopApplication;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:luca@camphuisen.com">Luca Camphuisen</a>
 * @since 6/13/20
 */
@SpringBootTest(classes = ShopApplication.class, webEnvironment =  SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class ShoppingCartControllerTest {

    private static final Logger log = LoggerFactory.getLogger(ShoppingCartControllerTest.class);
    @Autowired
    RestTemplateBuilder restTemplateBuilder;
    @Autowired
    ProductService productService;
    private TestRestTemplate template;


    @Before
    public void setUp() {
        log.info("Setup ShoppingCartControllerTest");
        RestTemplate customTemplate = restTemplateBuilder
                .rootUri("http://localhost:8349")
                .build();
        this.template = new TestRestTemplate(customTemplate,
                null, null, //I don't use basic auth, if you do you can set user, pass here
                TestRestTemplate.HttpClientOption.ENABLE_COOKIES); // I needed cookie support in this particular test, you may not have this need
    }


    @Test
    public void addItemToShoppingCart() throws Exception {
        //when(this.shoppingCartFacadeMock.addItemsToShoppingCart(any)).thenReturn(12345L);
        String jsonRequest = "{ \"productId\": 4, \"quantity\": 1 }";
        log.info("Pre post.");
        List<Product> products = productService.list();
        log.debug("Products: {}", products);
        for (Product product : products) {
            log.debug("Product: {}", product);
        }
        ShoppingCartItem requestItem = new ShoppingCartItem();
        requestItem.setProductId(4);
        requestItem.setQuantity(1);
        for(int i = 0; i < 5; i++) {
            ResponseEntity<?> responseEntity = template.postForEntity("/shop/cart/addShoppingCartItem", requestItem, Object.class);
            log.info("ResponseEntity: {}", responseEntity);
            assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));
        }
    }

    public class AddItemDto {
        private long productId;
        private long quantity;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AddItemDto that = (AddItemDto) o;
            return productId == that.productId &&
                    quantity == that.quantity;
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId, quantity);
        }

        @Override
        public String toString() {
            return "AddItemDto{" +
                    "productId=" + productId +
                    ", quantity=" + quantity +
                    '}';
        }

        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }
    }
}
