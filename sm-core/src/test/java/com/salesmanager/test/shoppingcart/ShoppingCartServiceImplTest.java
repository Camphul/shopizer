package com.salesmanager.test.shoppingcart;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.shoppingcart.ShoppingCartRepository;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartServiceImpl;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * CUSTOM-TEST-CASES unit tests for ShoppingCartServiceImpl
 *
 * @author <a href="mailto:luca@camphuisen.com">Luca Camphuisen</a>
 * @since 6/13/20
 */
public class ShoppingCartServiceImplTest {

    private ShoppingCartServiceImpl service;
    private ShoppingCartRepository cartRepository;

    @Before
    public void before() {
        this.cartRepository = mock(ShoppingCartRepository.class);
        this.service = new ShoppingCartServiceImpl(cartRepository);
    }

    @After
    public void after() {
        this.service = null;
        this.cartRepository = null;
    }

    @Test
    public void getById() {
        ShoppingCart shoppingCart = mock(ShoppingCart.class);
        long expectedId = 4;
        shoppingCart.setId(expectedId);
        /* Following two when statements are required due to the execution flow
         * of the getById method. Changing the return values in other tests
         * will allow us to gain higher test coverage and test all possible flows of the method.
         */
        when(shoppingCart.isObsolete()).thenReturn(false);
        when(shoppingCart.getLineItems()).thenReturn(Collections.EMPTY_SET);
        when(this.cartRepository.findOne(expectedId)).thenReturn(shoppingCart);
        ShoppingCart result = this.service.getById(expectedId);
        Assert.assertEquals("Expected shopping carts to be equal", shoppingCart, result);
    }

    @Test
    public void getByIdFindOneReturnsNull() {
        ShoppingCart shoppingCart = mock(ShoppingCart.class);
        long expectedId = 4;
        shoppingCart.setId(expectedId);
        when(shoppingCart.isObsolete()).thenReturn(false);
        when(shoppingCart.getLineItems()).thenReturn(Collections.EMPTY_SET);
        when(this.cartRepository.findOne(expectedId)).thenReturn(null);
        ShoppingCart result = this.service.getById(expectedId);
        Assert.assertNull("Expected null", result);
    }

    @Test
    public void getByIdReturnsNullObsoleteCart() throws ServiceException {
        ShoppingCart shoppingCart = mock(ShoppingCart.class);
        long expectedId = 4;
        shoppingCart.setId(expectedId);
        when(shoppingCart.isObsolete()).thenReturn(true);
        when(shoppingCart.getLineItems()).thenReturn(Collections.EMPTY_SET);
        when(this.cartRepository.findOne(expectedId)).thenReturn(shoppingCart);
        ShoppingCart result = this.service.getById(expectedId);
        Assert.assertNull("Expected null", result);
        verify(this.cartRepository, times(1)).delete(shoppingCart);
    }

    @Test
    public void removeShoppingCart() throws ServiceException {
        ShoppingCart shoppingCart = new ShoppingCart();
        long expectedId = 4;
        shoppingCart.setId(expectedId);
        this.service.removeShoppingCart(shoppingCart);
        verify(this.cartRepository, times(1)).delete(shoppingCart);
    }
}
