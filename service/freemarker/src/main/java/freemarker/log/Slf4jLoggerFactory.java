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
package freemarker.log;

/**
 * ��FreeMarkerʹ��SLF4j�������־��
 * 
 * @author Michael Zhou
 */
public class Slf4jLoggerFactory implements LoggerFactory {
    private final static String IMPL_CLASS_NAME = "com.alibaba.citrus.service.freemarker.impl.log.Slf4jLoggerFactoryImpl";
    private final PublicLoggerFactory factoryImpl;

    public Slf4jLoggerFactory() {
        try {
            factoryImpl = (PublicLoggerFactory) Thread.currentThread().getContextClassLoader()
                    .loadClass(IMPL_CLASS_NAME).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create Slf4jLoggerFactory", e);
        }
    }

    public Logger getLogger(String category) {
        return factoryImpl.getLogger(category);
    }
}
