package com.sunchaser.chunyu.graphql.kickstart.resolver.user.mutation;

import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.Part;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * graphql upload file mutation resolver
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/12
 */
@Component
@Slf4j
public class UploadFileMutation implements GraphQLMutationResolver {

    public String uploadFile(DataFetchingEnvironment environment) {
        log.info("Uploading file");

        // 获取graphql Servlet上下文
        DefaultGraphQLServletContext context = environment.getContext();

        // 获取文件对象javax.servlet.http.Part
        List<Part> fileParts = context.getFileParts();

        fileParts.forEach(part -> {
            // part.getInputStream();
            log.info("uploading: {}, size: {}", part.getSubmittedFileName(), part.getSize());
        });

        return UUID.randomUUID().toString();
    }
}
