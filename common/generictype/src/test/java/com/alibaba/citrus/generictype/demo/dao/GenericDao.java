/*
 * Copyright 2010 Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.alibaba.citrus.generictype.demo.dao;

import static com.alibaba.citrus.generictype.TypeInfo.*;
import static com.alibaba.citrus.util.Assert.*;

import java.io.Serializable;

import com.alibaba.citrus.generictype.ClassTypeInfo;
import com.alibaba.citrus.generictype.TypeInfo;

/**
 * ֧��DAO��generic���࣬ʵ����CRUD�����߼���
 * 
 * @author Michael Zhou
 */
public class GenericDao<DO extends DataObject<PK>, PK extends Object & Serializable> {
    private final ClassTypeInfo daoType;
    private final TypeInfo doType;

    public GenericDao() {
        this.daoType = factory.getClassType(getClass());

        ClassTypeInfo resolvedDaoType = (ClassTypeInfo) daoType.getSupertype(GenericDao.class).resolve(daoType);

        this.doType = resolvedDaoType.getActualTypeArgument("DO");
    }

    public void create(PK primaryKey, DO dataObject) {
        // ��δʵ��
    }

    public DO read(PK primaryKey) {
        try {
            @SuppressWarnings("unchecked")
            DO dataObject = (DO) doType.getRawType().newInstance();

            dataObject.setId(primaryKey);

            return dataObject;
        } catch (Exception e) {
            unexpectedException(e);
            return null;
        }
    }

    public void update(PK primaryKey, DO dataObject) {
        // ��δʵ��
    }

    public void delete(PK primaryKey) {
        // ��δʵ��
    }

    @Override
    public String toString() {
        return String.format("%s<%s>", daoType.getSimpleName(), doType.getSimpleName());
    }
}
