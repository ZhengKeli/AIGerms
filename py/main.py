import api
import core


def initialize():
    core.initialize()
    api.write_int(api.STU_SUCCEED)
    api.flush_stdout()


def finalize():
    core.finalize()
    api.write_int(api.STU_SUCCEED)
    api.flush_stdout()


def run_actor():
    val_feel_nutrient = api.read_vector_list()
    val_feel_germ = api.read_vector_list()
    val_act_velocity = core.run_actor(val_feel_nutrient, val_feel_germ)
    api.write_int(api.STU_SUCCEED)
    api.write_vector_list(val_act_velocity)
    api.flush_stdout()


def train_critic():
    val_feel_nutrient = api.read_vector_list()
    val_feel_germ = api.read_vector_list()
    val_act_velocity = api.read_vector_list()
    val_real_loss = api.read_float_list()
    core.train_critic(val_feel_nutrient, val_feel_germ, val_act_velocity, val_real_loss)
    api.write_int(api.STU_SUCCEED)
    api.flush_stdout()


def train_actor():
    val_feel_nutrient = api.read_vector_list()
    val_feel_germ = api.read_vector_list()
    core.train_actor(val_feel_nutrient, val_feel_germ)
    api.write_int(api.STU_SUCCEED)
    api.flush_stdout()


def main():
    while True:
        com = api.read_int()
        if com == api.COM_INITIALIZE:
            initialize()
        elif com == api.COM_FINALIZE:
            finalize()
            break
        elif com == api.COM_RUN_ACTOR:
            run_actor()
        elif com == api.COM_TRAIN_CRITIC:
            train_critic()
        elif com == api.COM_TRAIN_ACTOR:
            train_actor()
        else:
            print("the command is wrong!")
            break


main()
