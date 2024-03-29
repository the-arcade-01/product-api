package com.arcade.productapi.Controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.arcade.productapi.Model.ProductModel;
import com.arcade.productapi.Service.ProductService;

// import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        List<ProductModel> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductModel> getOneProduct(@PathVariable Long id) throws Exception {
        ProductModel product = productService.getOneProduct(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    void showImage(@PathVariable("id") Long id, HttpServletResponse response) throws Exception {
        ProductModel product = productService.getOneProduct(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(product.getData());
        response.getOutputStream().close();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws Exception {
        ProductModel product = productService.getOneProduct(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(product.getImageType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "product; filename=\"" + product.getImageName() + "\"")
                .body(new ByteArrayResource(product.getData()));
    }

    @PostMapping
    public ResponseEntity<ProductModel> createProduct(@RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Integer price,
            @RequestParam("file") MultipartFile file) throws Exception {

        ProductModel newProduct = productService.createProduct(name, description, price, file);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(@RequestBody ProductModel product, @RequestParam("file") MultipartFile file,
            @PathVariable Long id) throws Exception {
        Optional<Object> updatedProduct = productService.updateProduct(product, file, id);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
