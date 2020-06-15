package com.salesmanager.test.shoppingcart;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionType;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


/**
 * Integration tests for shopping cart.
 * @author Carl Samson
 */
public class ShoppingCartTest extends com.salesmanager.test.common.AbstractSalesManagerCoreTestCase {
    private MerchantStore store;
    private Language language;
    private ProductType generalType;

    @Before
    @Override
    public void init() throws ServiceException {
        super.init();//still want to invoke the super init
        this.store = merchantService.getByCode(MerchantStore.DEFAULT_STORE);
        this.language = languageService.getByCode("en");
        this.generalType = productTypeService.getProductType(ProductType.GENERAL_TYPE);
    }

    @After
    @Override
    public void close() throws ServiceException {
        super.close();//still want to invoke the super close
        this.store = null;
        this.language = null;
        this.generalType = null;
    }

    /**
     * Integration test to verify that we can create a shopping cart.
     *
     * @throws Exception
     */
    @Test
    @Rollback
    @Transactional
    public void createShoppingCart() throws Exception {
        Product product = buildSimpleProductSupport();
        this.productService.save(product);
        String cartCode = UUID.randomUUID().toString();
        ShoppingCart shoppingCart = buildSimpleShoppingCart(cartCode);
        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setShoppingCart(shoppingCart);

        FinalPrice price = pricingService.calculateProductPrice(product);

        item.setItemPrice(price.getFinalPrice());
        item.setQuantity(1);

        /** user selects black **/
        ProductAttribute blackAttribute = (ProductAttribute) product.getAttributes().toArray()[1];
        ShoppingCartAttributeItem attributeItem = new ShoppingCartAttributeItem(item, blackAttribute);
        item.getAttributes().add(attributeItem);

        shoppingCart.getLineItems().add(item);

        //create cart
        shoppingCartService.saveOrUpdate(shoppingCart);

        /** Retrieve cart **/

        ShoppingCart retrievedCart = shoppingCartService.getByCode(cartCode, store);
        assertNotNull("Retrieved cart cannot be null", retrievedCart);
        assertEquals(shoppingCart, retrievedCart);
    }
	/**
	 * Integration test to verify that an empty cart is not saved and null is returned.
	 *
	 * @throws Exception
	 */
	@Test
	@Rollback
	@Transactional
	public void createShoppingCartReturnsNullWhenEmptyOnCreate() throws Exception {
		String cartCode = UUID.randomUUID().toString();
		ShoppingCart shoppingCart = buildSimpleShoppingCart(cartCode);
		//create cart
		shoppingCartService.saveOrUpdate(shoppingCart);

		/** Retrieve cart **/

		ShoppingCart retrievedCart = shoppingCartService.getByCode(cartCode, store);
		assertNull("Retrieved cart must be null.", retrievedCart);
	}
    /**
     * Integration test to verify that we can create and delete a shopping cart.
     *
     * @throws ServiceException
     */
    @Test
    @Rollback
    @Transactional
    public void createAndDeleteShoppingCart() throws ServiceException {
        Product product = buildSimpleProductSupport();
        this.productService.save(product);
        String cartCode = UUID.randomUUID().toString();
        ShoppingCart shoppingCart = buildSimpleShoppingCart(cartCode);
        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setShoppingCart(shoppingCart);

        FinalPrice price = pricingService.calculateProductPrice(product);

        item.setItemPrice(price.getFinalPrice());
        item.setQuantity(1);

        /** user selects black **/
        ProductAttribute blackAttribute = (ProductAttribute) product.getAttributes().toArray()[1];
        ShoppingCartAttributeItem attributeItem = new ShoppingCartAttributeItem(item, blackAttribute);
        item.getAttributes().add(attributeItem);

        shoppingCart.getLineItems().add(item);

        //create cart
        shoppingCartService.saveOrUpdate(shoppingCart);

        /** Delete cart **/
        shoppingCartService.delete(shoppingCart);

        /** Check if cart has been deleted **/
        shoppingCart = shoppingCartService.getByCode(cartCode, store);

        assertNull(shoppingCart);
    }
    /**
     * Integration test to verify that we can create and add items to the shopping cart.
     *
     * @throws ServiceException
     */
    @Test
    @Rollback
    @Transactional
    public void createShoppingCartAndAddItem() throws ServiceException {
        Product product = buildSimpleProductSupport();
        Product product2 = buildSimpleProductSupport("TC12345", categoryService.getByCode(this.store, "shirts"), manufacturerService.getByCode(store, "addidas"),
                "coloz", "whitez", "blackz");
        this.productService.save(product);
        this.productService.save(product2);
        String cartCode = UUID.randomUUID().toString();
        ShoppingCart shoppingCart = buildSimpleShoppingCart(cartCode);
        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setShoppingCart(shoppingCart);

        FinalPrice price = pricingService.calculateProductPrice(product);

        item.setItemPrice(price.getFinalPrice());
        item.setQuantity(1);

        /** user selects black **/
        ProductAttribute blackAttribute = (ProductAttribute) product.getAttributes().toArray()[1];
        ShoppingCartAttributeItem attributeItem = new ShoppingCartAttributeItem(item, blackAttribute);
        item.getAttributes().add(attributeItem);

        shoppingCart.getLineItems().add(item);

        //create cart
        shoppingCartService.saveOrUpdate(shoppingCart);


        ShoppingCartItem item2 = new ShoppingCartItem(shoppingCart, product2);
        item2.setShoppingCart(shoppingCart);

        FinalPrice price2 = pricingService.calculateProductPrice(product2);

        item2.setItemPrice(price2.getFinalPrice());
        item2.setQuantity(1);

        /** user selects black **/
        ProductAttribute blackAttribute2 = (ProductAttribute) product2.getAttributes().toArray()[1];
        ShoppingCartAttributeItem attributeItem2 = new ShoppingCartAttributeItem(item2, blackAttribute2);
        item2.getAttributes().add(attributeItem2);
        item2.setShoppingCart(shoppingCart);
        shoppingCart.getLineItems().add(item2);
        shoppingCartService.saveOrUpdate(shoppingCart);


        /** Get retrieved **/
        ShoppingCart retrievedCart = shoppingCartService.getByCode(cartCode, store);
        Assert.assertNotNull(retrievedCart);
        //Check if contains both items
        Assert.assertEquals("Unequal line items", 2, retrievedCart.getLineItems().size());
        Assert.assertTrue("Does not contain correct line item product SKU's", retrievedCart.getLineItems().stream().map(lineItem -> lineItem.getProduct().getSku()).allMatch(
                sku -> product.getSku().equals(sku) || product2.getSku().equals(sku)
        ));
    }

	/**
	 * Integration test to verify that we can create and add items to the shopping cart.
	 *
	 * @throws ServiceException
	 */
	@Test(expected = ServiceException.class)
	@Rollback
	@Transactional
	public void createShoppingCartAndAddNullItemThrowsServiceExceptionOnSaveOrUpdate() throws ServiceException {
		Product product = buildSimpleProductSupport();
		this.productService.save(product);
		String cartCode = UUID.randomUUID().toString();
		ShoppingCart shoppingCart = buildSimpleShoppingCart(cartCode);
		ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
		item.setShoppingCart(shoppingCart);

		FinalPrice price = pricingService.calculateProductPrice(product);

		item.setItemPrice(price.getFinalPrice());
		item.setQuantity(1);

		/** user selects black **/
		ProductAttribute blackAttribute = (ProductAttribute) product.getAttributes().toArray()[1];
		ShoppingCartAttributeItem attributeItem = new ShoppingCartAttributeItem(item, blackAttribute);
		item.getAttributes().add(attributeItem);

		shoppingCart.getLineItems().add(item);

		//create cart
		shoppingCartService.saveOrUpdate(shoppingCart);
		shoppingCart = shoppingCartService.getByCode(cartCode, store);

		shoppingCart.getLineItems().add(null);
		shoppingCartService.saveOrUpdate(shoppingCart);
		shoppingCart = shoppingCartService.getByCode(cartCode, store);

		fail("Did not expect to succesfully save shopping cart.");
	}
	/**
	 * Integration test to verify that we can create and add items to the shopping cart.
	 * Adds a duplicate item and verifies behaviour.
	 *
	 * @throws ServiceException
	 */
	@Test
	@Rollback
	@Transactional
	public void createShoppingCartAndAddItemTwiceWithDifferentQuantities() throws ServiceException {
		Product product = buildSimpleProductSupport();
		Product product2 = buildSimpleProductSupport("TC12345", categoryService.getByCode(this.store, "shirts"), manufacturerService.getByCode(store, "addidas"),
				"coloz", "whitez", "blackz");
		this.productService.save(product);
		this.productService.save(product2);
		String cartCode = UUID.randomUUID().toString();
		ShoppingCart shoppingCart = buildSimpleShoppingCart(cartCode);
		ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
		item.setShoppingCart(shoppingCart);

		FinalPrice price = pricingService.calculateProductPrice(product);

		item.setItemPrice(price.getFinalPrice());
		item.setQuantity(1);

		/** user selects black **/
		ProductAttribute blackAttribute = (ProductAttribute) product.getAttributes().toArray()[1];
		ShoppingCartAttributeItem attributeItem = new ShoppingCartAttributeItem(item, blackAttribute);
		item.getAttributes().add(attributeItem);

		shoppingCart.getLineItems().add(item);

		//create cart
		shoppingCartService.saveOrUpdate(shoppingCart);


		ShoppingCartItem item2 = new ShoppingCartItem(shoppingCart, product2);
		item2.setShoppingCart(shoppingCart);

		FinalPrice price2 = pricingService.calculateProductPrice(product2);

		item2.setItemPrice(price2.getFinalPrice());
		item2.setQuantity(1);

		/** user selects black **/
//		ProductAttribute blackAttribute2 = (ProductAttribute) product2.getAttributes().toArray()[1];
//		ShoppingCartAttributeItem attributeItem2 = new ShoppingCartAttributeItem(item2, blackAttribute2);
//		item2.getAttributes().add(attributeItem2);
		item2.setShoppingCart(shoppingCart);
		shoppingCart.getLineItems().add(item2);
		shoppingCartService.saveOrUpdate(shoppingCart);


		/** Get retrieved **/
		ShoppingCart retrievedCart = shoppingCartService.getByCode(cartCode, store);
		Assert.assertNotNull(retrievedCart);
		//Check if contains both items
		Assert.assertEquals("Unequal line items1", 2, retrievedCart.getLineItems().size());
		Assert.assertTrue("Does not contain correct line item product SKU's1", retrievedCart.getLineItems().stream().map(lineItem -> lineItem.getProduct().getSku()).allMatch(
				sku -> product.getSku().equals(sku) || product2.getSku().equals(sku)
		));
		shoppingCart = retrievedCart;
		ShoppingCartItem item3 = new ShoppingCartItem(shoppingCart, product2);
		item3.setShoppingCart(shoppingCart);


		item3.setItemPrice(price2.getFinalPrice());
		item3.setQuantity(3);
		item3.setShoppingCart(shoppingCart);
		shoppingCart.getLineItems().add(item3);
		//shoppingCartService.
		this.shoppingCartService.saveOrUpdate(shoppingCart);

		retrievedCart = shoppingCartService.getByCode(cartCode, store);
		Assert.assertNotNull(retrievedCart);
		//Check if contains both items
		Assert.assertEquals("Unequal line items2", 2, retrievedCart.getLineItems().size());

		int qnty =retrievedCart.getLineItems().stream().filter(i -> product2.getSku().equals(i.getProduct().getSku())).findAny().map(ShoppingCartItem::getQuantity).orElseGet(() ->-123);
		Assert.assertEquals("Quantity did not meet exepected value", 3, qnty);
	}

    private Product buildSimpleProductSupport() throws ServiceException {
        /** CATALOG CREATION **/
        /**
         * Create the category
         */
        Category shirts = createCategorySupport("shirts", "Shirts");

        /**
         * Create a manufacturer
         */
        Manufacturer addidas = createManufacturerSupport("addidas", "Addidas");
        return buildSimpleProductSupport("TB12345", shirts, addidas, "color", "white", "black");
    }

    private Product buildSimpleProductSupport(String sku, Category category, Manufacturer manufacturer, String optionCode,
                                              String optionValueWhiteCode, String optionValueBlackCode) throws ServiceException {
        /**
         * Create an option
         */
        ProductOption option = new ProductOption();
        option.setMerchantStore(store);
        option.setCode(optionCode);
        option.setProductOptionType(ProductOptionType.Radio.name());

        ProductOptionDescription optionDescription = new ProductOptionDescription();
        optionDescription.setLanguage(language);
        optionDescription.setName("Color");
        optionDescription.setDescription("Item color");
        optionDescription.setProductOption(option);

        option.getDescriptions().add(optionDescription);

        productOptionService.saveOrUpdate(option);


        /** first option value **/
        ProductOptionValue white = createProductColorOptionValueSupport(optionValueWhiteCode, "White");


        ProductOptionValue black = createProductColorOptionValueSupport(optionValueBlackCode, "Black");


        /**
         * Create a complex product
         */
        Product product = new Product();
        product.setProductHeight(new BigDecimal(4));
        product.setProductLength(new BigDecimal(3));
        product.setProductWidth(new BigDecimal(1));
        product.setSku(sku);
        product.setManufacturer(manufacturer);
        product.setType(generalType);
        product.setMerchantStore(store);

        // Product description
        ProductDescription description = new ProductDescription();
        description.setName("Short sleeves shirt");
        description.setLanguage(language);
        description.setProduct(product);

        product.getDescriptions().add(description);
        product.getCategories().add(category);


        //availability
        ProductAvailability availability = new ProductAvailability();
        availability.setProductDateAvailable(new Date());
        availability.setProductQuantity(100);
        availability.setRegion("*");
        availability.setProduct(product);// associate with product

        //price
        ProductPrice dprice = new ProductPrice();
        dprice.setDefaultPrice(true);
        dprice.setProductPriceAmount(new BigDecimal(29.99));
        dprice.setProductAvailability(availability);


        ProductPriceDescription dpd = new ProductPriceDescription();
        dpd.setName("Base price");
        dpd.setProductPrice(dprice);
        dpd.setLanguage(language);

        dprice.getDescriptions().add(dpd);
        availability.getPrices().add(dprice);
        product.getAvailabilities().add(availability);


        //attributes
        //white
        ProductAttribute whiteAttribute = new ProductAttribute();
        whiteAttribute.setProduct(product);
        whiteAttribute.setProductOption(option);
        whiteAttribute.setAttributeDefault(true);
        whiteAttribute.setProductAttributePrice(new BigDecimal(0));//no price variation
        whiteAttribute.setProductAttributeWeight(new BigDecimal(0));//no weight variation
        whiteAttribute.setProductOption(option);
        whiteAttribute.setProductOptionValue(white);

        product.getAttributes().add(whiteAttribute);
        //black
        ProductAttribute blackAttribute = new ProductAttribute();
        blackAttribute.setProduct(product);
        blackAttribute.setProductOption(option);
        blackAttribute.setProductAttributePrice(new BigDecimal(5));//5 + dollars
        blackAttribute.setProductAttributeWeight(new BigDecimal(0));//no weight variation
        blackAttribute.setProductOption(option);
        blackAttribute.setProductOptionValue(black);

        product.getAttributes().add(blackAttribute);

        return product;
    }

    private ShoppingCart buildSimpleShoppingCart(String cartCode) throws ServiceException {

        /** Create Shopping cart **/
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(cartCode);
        return shoppingCart;
    }

    private Category createCategorySupport(String code, String name) throws ServiceException {
        Category category = new Category();
        category.setMerchantStore(store);
        category.setCode(code);

        CategoryDescription englishCategoryDescription = new CategoryDescription();
        englishCategoryDescription.setName(name);
        englishCategoryDescription.setCategory(category);
        englishCategoryDescription.setLanguage(language);

        Set<CategoryDescription> descriptions = new HashSet<>();
        descriptions.add(englishCategoryDescription);


        category.setDescriptions(descriptions);
        categoryService.create(category);
        return category;
    }

    private Manufacturer createManufacturerSupport(String code, String name) throws ServiceException {
        /**
         * Create a manufacturer
         */
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setMerchantStore(store);
        manufacturer.setCode(code);

        ManufacturerDescription manufacturerDescription = new ManufacturerDescription();
        manufacturerDescription.setLanguage(language);
        manufacturerDescription.setManufacturer(manufacturer);
        manufacturerDescription.setName(name);
        manufacturer.getDescriptions().add(manufacturerDescription);

        manufacturerService.create(manufacturer);
        return manufacturer;
    }

    private ProductOptionValue createProductColorOptionValueSupport(String code, String color) throws ServiceException {
        ProductOptionValue colorOption = new ProductOptionValue();
        colorOption.setMerchantStore(store);
        colorOption.setCode(code);

        ProductOptionValueDescription whiteDescription = new ProductOptionValueDescription();
        whiteDescription.setLanguage(language);
        whiteDescription.setName(color);
        whiteDescription.setDescription(color + " Color");
        whiteDescription.setProductOptionValue(colorOption);

        colorOption.getDescriptions().add(whiteDescription);

        productOptionValueService.saveOrUpdate(colorOption);
        return colorOption;
    }


}
