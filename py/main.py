import api
import core
from api import Command
from api import Status

sess = None

while True:
    com = api.read_int()
    if com == Command.INITIALIZE:
        core.initialize()
        api.write_int(Status.SUCCEED)
    elif com == Command.FINALIZE:
        core.finalize()
        api.write_int(Status.SUCCEED)
        break
    elif com == Command.RUN_ACTOR:
        val_feel_nutrient = api.read_vector_list()
        val_feel_germ = api.read_vector_list()
        val_act_velocity = core.run_actor(val_feel_nutrient, val_feel_germ)
        api.write_int(Status.SUCCEED)
        api.write_vector_list(val_act_velocity)
    elif com == Command.TRAIN_CRITIC:
        val_feel_nutrient = api.read_vector_list()
        val_feel_germ = api.read_vector_list()
        val_act_velocity = api.read_vector_list()
        val_real_loss = api.read_float_list()
        core.train_critic(val_feel_nutrient, val_feel_germ, val_act_velocity, val_real_loss)
        api.write_int(Status.SUCCEED)
    elif com == Command.TRAIN_ACTOR:
        val_feel_nutrient = api.read_vector_list()
        val_feel_germ = api.read_vector_list()
        core.train_actor(val_feel_nutrient, val_feel_germ)
        api.write_int(Status.SUCCEED)
