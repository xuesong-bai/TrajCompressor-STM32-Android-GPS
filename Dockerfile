FROM registry.cn-qingdao.aliyuncs.com/facedetect/centos7-python36:v2
MAINTAINER name SamYu,sam_miaoyu@foxmail.com
USER root
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir -p /file_and_log
COPY . /backend
COPY ./pip.conf /etc/pip.conf
WORKDIR /backend
RUN source ~/.bash_profile
RUN python3 -m pip install --upgrade pip
RUN python3 -m pip install --no-cache-dir -r requirements.txt
EXPOSE 10086
RUN chmod a+x start.sh
ENTRYPOINT ["./start.sh"]
