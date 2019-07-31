#!/usr/bin/env python
# -*- coding: utf-8 -*-
from flask import Flask, jsonify, abort
from flask_restplus import Api, Resource, fields, reqparse
from werkzeug.contrib.fixers import ProxyFix
import pymysql, sys
from pymysql.err import IntegrityError
from datetime import datetime
from loguru import logger
import time

app = Flask(__name__)
app.config['MAX_CONTENT_LENGTH'] = 1600 * 1024 * 1024
app.wsgi_app = ProxyFix(app.wsgi_app)
api = Api(app, version='0.1', title='Sensor Backend', description='middleware for sensor data')

# 表示/android下的后续路径
ns_android = api.namespace('android', description='connection to detect service')
# 表示/ios下的后续路径
ns_ios = api.namespace('ios', description='connection to detect service for iOS')

ns_STM32 = api.namespace('STM32', description = 'connection to detect service for STM32')

# 日志文件
logger.add("./file_and_log/db_log.log", rotation="100 MB")
logger.add(sys.stderr, format="{time} {level} {message}", filter="my_module", level="INFO")

"""
数据库类，连接安卓数据库。
建立了三个表，location,sensor,app
分别用于存放位置信息、传感器信息、应用使用记录信息
"""


class DB_android:
    def __init__(self):
        print('Connecting to db...')
        self.con = pymysql.Connect(
            host='x.x.x.x',
            port=3306,
            user='',
            passwd='',
            db='sensor_data_android',
            charset='utf8'
        )
        print('Connection success!')

    # CREATE TABLE `location` (
    #   `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    #   `id` char(32) NOT NULL,
    #   `longitude` double NOT NULL,
    #   `latitude` double NOT NULL,
    #   `altitude` double NOT NULL,
    #   `speed` double NOT NULL,
    #   `bearing` double NOT NULL,
    #   `accuracy` double NOT NULL,
    #   PRIMARY KEY (`time`,`id`)
    # ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    def add_locations(self, location_records):
        with self.con.cursor() as cur:
            for location_obj in location_records:
                try:
                    id = location_obj["id"]
                    longitude = location_obj["longitude"]
                    latitude = location_obj["latitude"]
                    altitude = location_obj["altitude"]
                    speed = location_obj["speed"]
                    bearing = location_obj["bearing"]
                    accuracy = location_obj["accuracy"]
                    tim = location_obj.get('time')
                    time_stamp = datetime.fromtimestamp(tim).strftime('%Y-%m-%d %H:%M:%S')
                    sql = "INSERT INTO `location`(`time`, `id`, `longitude`, `latitude`, `altitude`, `speed`, `bearing`, `accuracy`) VALUES (%s,%s,%s,%s,%s,%s,%s,%s)"
                    cur.execute(sql, (time_stamp, id, longitude, latitude, altitude, speed, bearing, accuracy))
                except IntegrityError:
                    pass
                except Exception as e:
                    logger.exception(e)
                    return False
        self.con.commit()
        return True

    # CREATE TABLE `sensor` (
    #   `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    #   `id` char(32) NOT NULL,
    #   `type` int(11) NOT NULL,
    #   `value` double DEFAULT NULL,
    #   `x` double DEFAULT NULL,
    #   `y` double DEFAULT NULL,
    #   `z` double DEFAULT NULL,
    #   PRIMARY KEY (`time`,`id`)
    # ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    def add_sensors(self, sensor_records):
        with self.con.cursor() as cur:
            for obj in sensor_records:
                try:
                    tim = obj['time']
                    id = obj["id"]
                    type = obj['type']
                    value = obj.get('value')
                    x = obj.get('x')
                    y = obj.get('y')
                    z = obj.get('z')
                    time_stamp = datetime.fromtimestamp(tim).strftime('%Y-%m-%d %H:%M:%S')
                    if value:
                        sql = "INSERT INTO `sensor`(`time`, `id`, `type`, `value`) VALUES (%s,%s,%s,%s)"
                        cur.execute(sql, (time_stamp, id, type, value))
                    if not value:
                        sql = "INSERT INTO `sensor`(`time`, `id`, `type`, `x`, `y`, `z`) VALUES (%s,%s,%s,%s,%s,%s)"
                        cur.execute(sql, (time_stamp, id, type, x, y, z))
                except IntegrityError:
                    pass
                except Exception as e:
                    logger.exception(e)
                    return False
        self.con.commit()
        return True

    # CREATE TABLE `app` (
    #   `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    #   `id` char(32) NOT NULL,
    #   `appName` varchar(200) NOT NULL,
    #   `startTime` double NOT NULL,
    #   `endTime` double NOT NULL,
    #   PRIMARY KEY (`time`,`id`)
    # ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    def add_app_record(self, app_records):
        with self.con.cursor() as cur:
            for obj in app_records:
                try:
                    tim = obj['time']
                    id = obj["id"]
                    appName = obj['appName']
                    start = obj['startTime']
                    end = obj['endTime']
                    time_stamp = datetime.fromtimestamp(tim).strftime('%Y-%m-%d %H:%M:%S')
                    sql = "INSERT INTO `app`(`time`, `id`, `appName`, `startTime`, `endTime`) VALUES (%s,%s,%s,%s,%s)"
                    cur.execute(sql, (time_stamp, id, appName, start, end))
                except IntegrityError:
                    pass
                except Exception as e:
                    logger.exception(e)
                    return False
        self.con.commit()
        return True

    def __del__(self):
        self.con.close()


class DB_iOS:
    def __init__(self):
        print('Connecting to db...')
        self.con = pymysql.Connect(
            host='x.x.x.x',
            port=3306,
            user='',
            passwd='',
            db='sensor_data_ios',
            charset='utf8'
        )
        print('Connection success!')

    def add_locations(self, location_records):
        with self.con.cursor() as cur:
            for location_obj in location_records:
                try:
                    tim = location_obj["time"]
                    id = location_obj["id"]
                    longitude = location_obj["longitude"]
                    latitude = location_obj["latitude"]
                    altitude = location_obj["altitude"]
                    time_gps = location_obj["time_gps"]
                    speed = location_obj["speed"]
                    course = location_obj["course"]
                    accuracy_horizontal = location_obj["accuracy_horizontal"]
                    accuracy_vertical = location_obj["accuracy_vertical"]
                    sql = "INSERT INTO `location`(`time`, `id`, `longitude`, `latitude`, `altitude`, `time_gps`, `speed`, `course`, `accuracy_horizontal`, `accuracy_vertical`) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
                    cur.execute(sql, (
                    tim, id, longitude, latitude, altitude, time_gps, speed, course, accuracy_horizontal,
                    accuracy_vertical))
                except IntegrityError:
                    pass
                except Exception as e:
                    logger.exception(e)
                    return False
        self.con.commit()
        return True

    def add_sensors(self, sensor_records):
        with self.con.cursor() as cur:
            for sensor_obj in sensor_records:
                try:
                    tim = sensor_obj["time"]
                    id = sensor_obj["id"]
                    accelerometerX = sensor_obj["accelerometerX"]
                    accelerometerY = sensor_obj["accelerometerY"]
                    accelerometerZ = sensor_obj["accelerometerZ"]
                    gyroscopeDataX = sensor_obj["gyroscopeDataX"]
                    gyroscopeDataY = sensor_obj["gyroscopeDataY"]
                    gyroscopeDataZ = sensor_obj["gyroscopeDataZ"]
                    magnetometerX = sensor_obj["magnetometerX"]
                    magnetometerY = sensor_obj["magnetometerY"]
                    magnetometerZ = sensor_obj["magnetometerZ"]
                    sql = "INSERT INTO `sensor`(`time`, `id`, `accelerometerX`, `accelerometerY`, `accelerometerZ`, `gyroscopeDataX`, `gyroscopeDataY`, `gyroscopeDataZ`, `magnetometerX`, `magnetometerY`, `magnetometerZ`) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
                    cur.execute(sql, (
                    tim, id, accelerometerX, accelerometerY, accelerometerZ, gyroscopeDataX, gyroscopeDataY,
                    gyroscopeDataZ, magnetometerX, magnetometerY, magnetometerZ))
                except IntegrityError:
                    pass
                except Exception as e:
                    logger.exception(e)
                    return False
        self.con.commit()
        return True

    def __del__(self):
        self.con.close()


@ns_android.route('/upload')
class init(Resource):
    # @ns_android.expect(resource_parser,validate=True)
    def post(self):
        """
        解析数据记录
        """
        data_records = api.payload
        db = DB_android()
        sensor_res = db.add_sensors(data_records.get('sensor')) if data_records.get('sensor') else True
        location_res = db.add_locations(data_records.get('location')) if data_records.get('location') else True
        app_res = db.add_app_record(data_records.get('app')) if data_records.get('app') else True

        if all([sensor_res, location_res, app_res]):
            return jsonify({'status': 'success'})
        else:
            abort(400)


@ns_ios.route('/upload')
class init(Resource):
    def post(self):
        data_records = api.payload
        db = DB_iOS()
        location_res = db.add_locations(data_records.get('location')) if data_records.get('location') else True
        sensor_res = db.add_sensors(data_records.get('sensor')) if data_records.get('sensor') else True

        if all([location_res, sensor_res]):
            return jsonify({'status': 'success'})
        else:
            abort(400)


class DB_STM32:
    def __init__(self):
        print('Connecting to db...')
        self.con = pymysql.Connect(
            host='x.x.x.x',
            port=3306,
            user='',
            passwd='',
            db='GPSData_Compressed',
            charset='utf8'
            )
        print('Connection success!')


    def add_Edges(self, Edges_records):
        for Edges_obj in Edges_records:
            try:
                id = Edges_obj["id"]
                tim = Edges_obj["time"]
                if(tim < 1000000000):
                    tim = tim + 1000000000;
                order = 6
                edges = Edges_obj["edge"]
                edges = edges.split(';', -1)
                for edge in edges:
                    cols = edge.split(',', -1)
                    with self.con.cursor() as cur:
                        sql = "INSERT INTO `GPSData_Bai`(`id`, `time`, `time_insert`, `node1_lon`, `node1_lat`, `node2_lon`, `node2_lat`) VALUES (%s, %s, %s, %s, %s, %s, %s)"
                        t = time.time()
                        cur.execute(sql, (id, tim, float(t), float(cols[0]), float(cols[1]), float(cols[2]), float(cols[3])))
                        tim = tim + order
                    self.con.commit()
            except pymysql.err.IntegrityError:
                continue
            except Exception as e:
                logger.exception(e)
                return False
        
        return True
                    

    def __del__(self):
        self.con.close()



@ns_STM32.route('/upload')
class init(Resource):
    def post(self):
        data_records = api.payload
        logger.debug(data_records)
        db = DB_STM32()
        Edge_res = db.add_Edges(data_records.get('Edge')) if data_records.get('Edge') else True
#        location_res = db.add_locations(data_records.get('location')) if data_records.get('location') else True
#        sensor_res = db.add_sensors(data_records.get('sensor')) if data_records.get('sensor') else True

        if Edge_res:#all([location_res, sensor_res])
            return jsonify({'status':'success'})
        else:
            abort(400)



# gps_record_field = {
#         'time':fields.Integer(required = True),
#         'id' : fields.String(required = True),
#         'longitude': fields.Float(required = True),
#         'latitude': fields.Float(required = True),
#         'altitude': fields.Float(required = True),
#         'speed': fields.Float(required = True),
#         'bearing': fields.Float(required = True),
#         'accuracy': fields.Float(required = True)
#     }
#
# sensor_record_field = {
#     'time': fields.Integer(required = True),
#     'id': fields.String(required = True),
#     'type': fields.Integer(required = True),
#     'value': fields.Float,
#     'x': fields.Float,
#     'y': fields.Float,
#     'z': fields.Float
# }
#
# app_time_field = {
#     'time': fields.Integer(required = True),
#     'id': fields.String(required=True),
#     'appName': fields.String(required=True),
#     'startTime': fields.Integer(required=True),
#     'endTime': fields.Integer(required=True)
# }
#
# resource_field = {
#     'location': fields.List(fields.(gps_record_field),description='list of GPS location record'),
#     'sensor' : fields.List(fields.Nested(sensor_record_field),description='List of sensor data record'),
#     'app': fields.List(fields.Nested(app_time_field),description='List of app time record')
# }


#
# def location_validator(value):
#     LOCATION_SCHEMA = {
#         'time': {'required': True, 'type': 'integer'},
#         'id': {'required': True, 'type': 'string'},
#         'longitude': {'required': True, 'type': 'float'},
#         'latitude': {'required': True, 'type': 'float'},
#         'altitude': {'required': True, 'type': 'float'},
#         'speed': {'required': True, 'type': 'float'},
#         'bearing': {'required': True, 'type': 'float'},
#         'accuracy': {'required': True, 'type': 'float'},
#     }
#     v = Validator(LOCATION_SCHEMA)
#     if v.validate(value):
#         return value
#     else:
#         raise ValueError(json.dumps(v.errors))
#
# def sensor_validator(value):
#     SENSOR_SCHEMA = {
#         'time': {'required': True, 'type': 'integer'},
#         'id': {'required': True, 'type': 'string'},
#         'type':  {'required': True, 'type': 'integer'},
#         'value':{ 'type': 'float'},
#         'x': { 'type': 'float'},
#         'y': { 'type': 'float'},
#         'z': { 'type': 'float'}
#     }
#     v = Validator(SENSOR_SCHEMA)
#     if v.validate(value):
#         return value
#     else:
#         raise ValueError(json.dumps(v.errors))
#
# def app_validator(value):
#     SENSOR_SCHEMA = {
#         'time': {'required': True, 'type': 'integer'},
#         'id': {'required': True, 'type': 'string'},
#         'appName': {'required': True, 'type': 'string'},
#         'startTime': {'required': True, 'type': 'integer'},
#         'endTime': {'required': True, 'type': 'integer'}
#     }
#     v = Validator(SENSOR_SCHEMA)
#     if v.validate(value):
#         return value
#     else:
#         raise ValueError(json.dumps(v.errors))
#
#
# resource_parser = reqparse.RequestParser()
# resource_parser.add_argument('location',type=location_validator,action='append')
# resource_parser.add_argument('sensor',type=sensor_validator,action='append')
# resource_parser.add_argument('app',type=app_validator,action='append')
