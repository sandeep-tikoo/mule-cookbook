package com.cookbook.tutorial.service;

import com.cookbook.tutorial.internal.service.CookBookDefaultBackEndImp;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Mulesoft.
 */
public class CRUDTest {
    MuleCookBookServiceImpl server;
    Ingredient created;
    String token;
    @Before
    public void setup() throws SessionExpiredException, InvalidEntityException, InvalidTokenException, InvalidCredentialsException {
        server = new MuleCookBookServiceImpl();
        server.setServiceDAL(new CookBookDefaultBackEndImp());
        token = server.login("admin","admin");
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Foo");
        Create createRequest = new Create();
        createRequest.setEntity(ingredient);
        created = (Ingredient) server.create(createRequest,token).getReturn();
    }

    @Test
    public void getRecentlyAdded(){
        List<Recipe> recentlyAdded = server.getRecentlyAdded();
        assertFalse(recentlyAdded.isEmpty());
    }

    @Test
    public void testGet() throws SessionExpiredException, InvalidEntityException, NoSuchEntityException, InvalidTokenException {
        Get get = new Get();
        get.setId(created.getId());
        Ingredient retrieved = (Ingredient) server.get(get,token).getReturn();
        assertSame(created, retrieved);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testGetWithWrongId() throws SessionExpiredException, InvalidEntityException, NoSuchEntityException, InvalidTokenException {
        Get get=new Get();
        get.setId(5);
        server.get(get,token);
    }

    @Test
    public void testUpdate() throws SessionExpiredException, InvalidEntityException, NoSuchEntityException, InvalidTokenException {
        String name = "SSSDDDSSSDD";
        created.setName(name);
        Update update = new Update();
        update.setEntity(created);
        server.update(update,token);
        Get get = new Get();
        get.setId(created.getId());
        Ingredient retrieved = (Ingredient)server.get(get,token).getReturn();
        assertEquals(retrieved.getName(), name);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testUpdateNoneExistingEntity() throws SessionExpiredException, InvalidEntityException, NoSuchEntityException, InvalidTokenException {
        String name = "SSSDDDSSSDD";
        Ingredient ing = new Ingredient();
        ing.setName(name);
        ing.setId(5);
        Update update = new Update();
        update.setEntity(ing);
        server.update(update,token);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testDelete() throws SessionExpiredException, InvalidEntityException, NoSuchEntityException, InvalidTokenException {
        Delete delete = new Delete();
        delete.setId(created.getId());
        server.delete(delete,token);
        Get get = new Get();
        get.setId(created.getId());
        server.get(get,token);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testDeleteNoneExisting() throws SessionExpiredException, InvalidEntityException, NoSuchEntityException, InvalidTokenException {
        Delete delete = new Delete();
        delete.setId(5);
        server.delete(delete,token);
    }

    @Test
    public void getList() throws SessionExpiredException, InvalidEntityException, NoSuchEntityException, InvalidTokenException {
        GetList ids = new GetList();
        ids.getEntityIds().add(1);
        ids.getEntityIds().add(2);
        GetListResponse response =server.getList(ids, token);
        assertEquals(2,response.getReturn().size());
    }
}