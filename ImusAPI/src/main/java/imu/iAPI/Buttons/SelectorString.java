package imu.iAPI.Buttons;

import imu.iAPI.Enums.VALUE_TYPE;

public class SelectorString
{
    private String _string;
    private VALUE_TYPE _type;
    private String _value;

    public SelectorString(String string, String value, VALUE_TYPE type)
    {
        _string = string;
        _type = type;
        _value = value;
    }

    public SelectorString(String string, int value)
    {
        _string = string;
        _type = VALUE_TYPE.INT;
        _value = String.valueOf(value);
    }

    public SelectorString(String string, double value)
    {
        _string = string;
        _type = VALUE_TYPE.DOUBLE;
        _value = String.valueOf(value);
    }

    public SelectorString(String string, boolean value)
    {
        _string = string;
        _type = VALUE_TYPE.BOOLEAN;
        _value = String.valueOf(value);
    }


    public String getStringWithValue()
    {
        if(_type == VALUE_TYPE.NONE)
            return _string;

        return _string.replace("%value%", _value);
    }

    public String get_string()
    {
        return _string;
    }

    public void set_string(String _string)
    {
        this._string = _string;
    }

    public VALUE_TYPE get_type()
    {
        return _type;
    }

    public void set_type(VALUE_TYPE _type)
    {
        this._type = _type;
    }

    public String get_value()
    {
        return _value;
    }

    public void set_value(String _value)
    {
        this._value = _value;
    }
}
