package ru.otus.http.jserver.application;

import ru.otus.http.jserver.Application;
import ru.otus.http.jserver.BadRequestExceptionEx;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class ProductsService {

    private static final String DATABASE_URL = "jdbc:sqlite:./src/main/resources/database.db";
    private static final String PRODUCT_INSERT = "insert into products (title) values (?);";
    private static final String GET_PRODUCT_BY_ID = "select * from products where id = ?;";
    private static final String GET_ALL_PRODUCTS = "select * from products;";
    private static final String PRODUCT_UPDATE = "update products set title = ? where id = ?;";
    private static final String PRODUCT_DELETE = "delete from products where id = ?;";
    private static final String DELETE_ALL_PRODUCTS = "delete from products;";
    private static final String GET_PRODUCT_BY_TITLE = "select * from products where title = ?;";


    private final Connection connection;

    private List<Product> products;

    public ProductsService() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        int responceBodySize = 0;
        Product product;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(GET_ALL_PRODUCTS)) {
                if (!resultSet.isBeforeFirst()) {
                    throw new BadRequestExceptionEx("404 Not Found", "Список продуктов пуст");
                }
                while (resultSet.next()) {
                    product = new Product(resultSet.getLong("id"), resultSet.getString("title"));
                    products.add (product);
                    responceBodySize+= product.toString().length();
                    if (responceBodySize > Application.limitResponceBody) {
                        throw new BadRequestExceptionEx ("413 Payload Too Large", "Payload Too Large");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return products;
    }


    public Product getProductById(Long id) {
        try (PreparedStatement prStatement = connection.prepareStatement(GET_PRODUCT_BY_ID)) {
            prStatement.setLong(1, id);
            try (ResultSet resultSet = prStatement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    throw new BadRequestExceptionEx("404 Not Found", "Не найден продукт с идентификатором " + id);
                }
                return new Product(resultSet.getLong("id"), resultSet.getString("title"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean createNewProduct(String title, Product product) {
        try (PreparedStatement prStatement = connection.prepareStatement(GET_PRODUCT_BY_TITLE)) {
            prStatement.setString(1, title);
            try (ResultSet resultSet = prStatement.executeQuery()) {
                if (resultSet.isBeforeFirst()) {
                    product.setId(resultSet.getLong("id"));
                    product.setTitle(resultSet.getString("title"));
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement prStatement = connection.prepareStatement(PRODUCT_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            prStatement.setString(1, title);
            prStatement.executeUpdate();
            try (ResultSet resultSet = prStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    product.setId(resultSet.getLong(1));
                    product.setTitle(title);
                    return true;
                } else {
                    throw new RuntimeException("INSERT: Ошибка при получении id продукта");
                }

            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateByNewProduct(Product product) {
        try (PreparedStatement prStatement = connection.prepareStatement(PRODUCT_UPDATE)) {
            prStatement.setString(1, product.getTitle());
            prStatement.setLong(2, product.getId());
            if (prStatement.executeUpdate() == 0) {
                throw new BadRequestExceptionEx("404 Not Found", "Не найден продукт с идентификатором " + product.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteProductById(Long id) {
        try (PreparedStatement prStatement = connection.prepareStatement(PRODUCT_DELETE)) {
            prStatement.setLong(1, id);
            if (prStatement.executeUpdate() == 0) {
                throw new BadRequestExceptionEx("404 Not Found", "Не найден продукт с идентификатором " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteAllProducts() {
        try (Statement statement = connection.createStatement()) {
            if (statement.executeUpdate(DELETE_ALL_PRODUCTS) == 0) {
                throw new BadRequestExceptionEx("404 Not Found", "Справочник продуктов пуст");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
