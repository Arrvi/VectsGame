package eu.arrvi.vects.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kris on 2014-12-18.
 */
public abstract class AdvancedCommandEventAdapter implements CommandEventListener {
    private Map<String, Method> bindingMap;
    
    private void bindMethods() {
        bindingMap = new HashMap<>();
        
        Class<? extends AdvancedCommandEventAdapter> obj = this.getClass();
    
        for (Method method : obj.getDeclaredMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            
            if (!method.isAccessible()) continue;
            if (parameterTypes.length != 1 ) continue;
            if (!parameterTypes[0].isAssignableFrom(CommandEvent.class)) continue;
            
            if (method.isAnnotationPresent(BindCommand.class)) {
                BindCommand binding = method.getAnnotation(BindCommand.class);
                bindingMap.put(binding.value(), method);
            }
        }
    }
    
    @Override
    public void commandReceived(CommandEvent event) {
        if ( bindingMap == null ) bindMethods();
        
        Method method = null;

        if (bindingMap.containsKey(event.getCommand().getName())) {
            method = bindingMap.get(event.getCommand().getName());
        }
        else {
            try {
                method = getClass().getMethod("unknownCommand", CommandEvent.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        if (method != null) {
            try {
                method.invoke(this, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected abstract void unknownCommand(CommandEvent command); 
}
